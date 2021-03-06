package com.sam.jcc.cloud.providers;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sam.jcc.cloud.app.AppMetadata;
import com.sam.jcc.cloud.app.AppProvider;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.impl.JenkinsProvider;
import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.dataprovider.impl.MySqlDataProvider;
import com.sam.jcc.cloud.dataprovider.impl.MySqlDatabaseManager;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import com.sam.jcc.cloud.project.ProjectMetadata;
import com.sam.jcc.cloud.project.impl.MavenProjectProvider;
import com.sam.jcc.cloud.util.GitDaemon;
import com.sam.jcc.cloud.util.TestEnvironment;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.git.impl.GitProtocolProvider;
import com.sam.jcc.cloud.vcs.git.impl.GitRemoteStorage;

/**
 * @author Alexey Zhytnik
 * @since 19-Jan-17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProvidersIntegrationTest extends TestEnvironment {

	static final String PROJECT_ARTIFACT_ID = "iproject" + new Random().nextInt(1000);
	static final String PROJECT_GROUP_ID = "com.samsolutions";

	@Autowired
	AppProvider apps;

	@Autowired
	JenkinsProvider jenkins;

	@Autowired
	GitProtocolProvider git;

	@Autowired
	MySqlDataProvider mySqlInjector;

	@Autowired
	MavenProjectProvider mavenGenerator;

	AppMetadata app;

	AppData data;
	CIProject job;
	ProjectMetadata metadata;
	VCSRepository repository;

	@Before
	public void setUp() throws IOException {
		job = job();
		app = app();
		data = data(app);
		metadata = mavenProject(data);
		repository = repository();

		mySqlManager.drop(data);
		apps.findAll().forEach(apps::delete);

		jenkins.setJenkins(TestEnvironment.jenkins);
		setUpGitRemoteStorage(TestEnvironment.daemon);
	}

	@After
	public void tearDown() {
		mySqlManager.drop(data);
	}

	@Test
	public void createsAndGeneratesAndInjectsMySqlAndBuildsOnJenkins() throws Exception {
		apps.create(app);

		final byte[] sources = readSources(mavenGenerator.update(metadata));
		assertThat(sources).isNotEmpty();

		loadAndCopySourcesTo(job, data, repository);
		git.create(repository);

		mySqlInjector.update(data);
		assertThat(data.getSources()).isNotEqualTo(sources);

		loadAndCopySourcesTo(repository);
		git.update(repository);

		clearLocalSources(repository);
		copySourcesTo(git.read(repository), job, data);

		jenkins.create(job);
		waitWhileProcessing(job);
		assertThat(getBuild(jenkins.read(job))).isNotEmpty();

		deleteQuietly(job);
		disableGitSupport(repository);
		apps.delete(app);
	}

	AppMetadata app() {
		final AppMetadata app = new AppMetadata();
		app.setProjectName(PROJECT_ARTIFACT_ID);
		app.setType("maven-project");
		return app;
	}

	ProjectMetadata mavenProject(AppData appData) {
		final ProjectMetadata metadata = new ProjectMetadata();

		metadata.setJavaVersion("1.8");
		metadata.setGroupId(PROJECT_GROUP_ID);
		metadata.setArtifactId(appData.getAppName());
		metadata.setProjectType("maven-project");
		metadata.setBootVersion("1.4.3.RELEASE");

		metadata.setBasePackage(PROJECT_GROUP_ID + "." + metadata.getArtifactId());
		metadata.setDependencies(singletonList("web"));

		metadata.setProjectName("iProject");
		metadata.setVersion("0.0.1-SNAPSHOT");
		metadata.setWebAppPackaging(false);
		metadata.setDescription("Project for integration test");

		return metadata;
	}

	AppData data(AppMetadata appMetadata) {
		final AppData data = new AppData();
		data.setAppName(appMetadata.getProjectName());
		return data;
	}

	VCSRepository repository() {
		final VCSRepository repo = new VCSRepository();
		repo.setArtifactId(PROJECT_ARTIFACT_ID);
		return repo;
	}

	CIProject job() {
		final CIProject job = new CIProject();
		job.setArtifactId(PROJECT_ARTIFACT_ID);
		return job;
	}

	@Autowired
	FileManager files;

	@Autowired
	ZipArchiveManager zipManager;

	@Autowired
	MySqlDatabaseManager mySqlManager;

	@Autowired
	ProjectDataRepository dataRepository;

	void setUpGitRemoteStorage(GitDaemon daemon) {
		final GitRemoteStorage storage = new GitRemoteStorage();
		storage.setBaseRepository(daemon.getStorage());
		storage.setPort(daemon.getCurrentPort());

		git.getGit().setStorage(storage);
	}

	byte[] readSources(IProjectMetadata metadata) {
		return ((ProjectMetadata) metadata).getProjectSources();
	}

	void loadAndCopySourcesTo(CIProject job, AppData data, VCSRepository repo) throws IOException {
		final byte[] sources = loadProjectSources();
		final File zip = temp.newFile(), dir = temp.newFolder();

		files.write(sources, zip);
		zipManager.unzip(zip, dir);

		job.setSources(dir);
		repo.setSources(dir);
		data.setSources(sources);
	}

	void clearLocalSources(VCSRepository repo) throws IOException {
		repo.setSources(temp.newFolder());
	}

	void copySourcesTo(IVCSMetadata metadata, CIProject job, AppData data) {
		final VCSRepository repo = (VCSRepository) metadata;

		final File dir = repo.getSources();
		job.setSources(dir);

		final byte[] sources = zipManager.zip(dir);
		data.setSources(sources);
	}

	void loadAndCopySourcesTo(VCSRepository repo) throws IOException {
		final File zip = temp.newFile(), dir = temp.newFolder();

		files.write(loadProjectSources(), zip);
		zipManager.unzip(zip, dir);
		repo.setSources(dir);
	}

	byte[] loadProjectSources() {
		return dataRepository.findByName(PROJECT_ARTIFACT_ID).orElseThrow(RuntimeException::new).getSources();
	}

	byte[] getBuild(ICIMetadata metadata) {
		final CIProject job = (CIProject) jenkins.read(metadata);
		return job.getBuild();
	}

	void disableGitSupport(VCSRepository repo) throws IOException {
		daemon.disableExport(repo);
		git.delete(repo);
	}
}
