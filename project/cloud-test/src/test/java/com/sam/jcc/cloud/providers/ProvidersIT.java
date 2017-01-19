package com.sam.jcc.cloud.providers;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.impl.JenkinsProvider;
import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.dataprovider.impl.MySqlDataProvider;
import com.sam.jcc.cloud.dataprovider.impl.MySqlDatabaseManager;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import com.sam.jcc.cloud.project.ProjectMetadata;
import com.sam.jcc.cloud.project.impl.MavenProjectProvider;
import com.sam.jcc.cloud.util.TestEnvironment;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.git.GitAbstractStorage;
import com.sam.jcc.cloud.vcs.git.impl.GitProtocolProvider;
import org.bouncycastle.crypto.RuntimeCryptoException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 19-Jan-17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProvidersIT extends TestEnvironment {

    @Autowired
    JenkinsProvider jenkins;

    @Autowired
    GitProtocolProvider git;

    @Autowired
    MySqlDataProvider mySqlInjector;

    @Autowired
    MavenProjectProvider mavenGenerator;

    AppData app;
    CIProject job;
    ProjectMetadata metadata;
    VCSRepository repository;

    @Before
    public void setUp() throws IOException {
        metadata = mavenMetadata();

        app = app();
        mySqlManager.drop(app);

        repository = repository();
        job = job();

        jenkins.setJenkins(TestEnvironment.jenkins);
        setGitFolder(TestEnvironment.daemon.getStorage());
    }

    @Test
    public void generatesAndInjectsMySqlAndBuildsOnJenkins() throws Exception {
        byte[] sources = readSources(mavenGenerator.create(metadata));
        assertThat(sources).isNotEmpty();

        saveSourcesTo(sources, job, repository);
        git.create(repository);

        jenkins.create(job);
        waitWhileProcessing(job);
        assertThat(hasBuild(jenkins.read(job))).isTrue();


        mySqlInjector.update(app);
        updateSources(repository, app);
        git.update(repository);

        removeSources(repository);
        readSourcesTo(git.read(repository), job, app);

        jenkins.update(job);
        waitWhileProcessing(job);
        assertThat(hasBuild(jenkins.read(job))).isTrue();


        deleteQuietly(job);

        daemon.disableExport(repository);
        git.delete(repository);
    }

    ProjectMetadata mavenMetadata() {
        final ProjectMetadata metadata = new ProjectMetadata();

        metadata.setJavaVersion("1.8");
        metadata.setArtifactId("iproject");
        metadata.setGroupId("com.sam.jcc.cloud");
        metadata.setProjectType("maven-project");
        metadata.setBootVersion("1.4.3.RELEASE");

        metadata.setBasePackage("com.sam.jcc.cloud.iproject");
        metadata.setDependencies(singletonList("web"));

        metadata.setProjectName("iProject");
        metadata.setVersion("0.0.1-SNAPSHOT");
        metadata.setWebAppPackaging(false);
        metadata.setDescription("Project for integration test");

        return metadata;
    }

    AppData app() {
        final AppData app = new AppData();
        app.setAppName(metadata.getArtifactId());

        applyFix();
        return app;
    }

    VCSRepository repository() {
        final VCSRepository repo = new VCSRepository();

        repo.setName(metadata.getArtifactId());
        repo.setGroupId(metadata.getGroupId());
        repo.setArtifactId(metadata.getArtifactId());
        return repo;
    }

    CIProject job() {
        final CIProject job = new CIProject();

        job.setArtifactId(metadata.getArtifactId());
        job.setGroupId(metadata.getGroupId());
        return job;
    }

    /* TEST INFRASTRUCTURE */

    byte[] readSources(IProjectMetadata metadata) {
        return ((ProjectMetadata) metadata).getProjectSources();
    }

    void saveSourcesTo(byte[] sources, CIProject job, VCSRepository repo) throws IOException {
        final File zip = temp.newFile(), dir = temp.newFolder();

        files.write(sources, zip);
        zipManager.unzip(zip, dir);

        repo.setSources(dir);
        job.setSources(dir);

        applyFix(sources);
    }

    void removeSources(VCSRepository repo) throws IOException {
        repo.setSources(temp.newFolder());
    }

    void updateSources(VCSRepository repo, AppData data) throws IOException {
        final File zip = temp.newFile(), dir = temp.newFolder();

        files.write(data.getSources(), zip);
        zipManager.unzip(zip, dir);
        repo.setSources(dir);
    }

    void readSourcesTo(IVCSMetadata metadata, CIProject job, AppData data){
        final VCSRepository repo = (VCSRepository) metadata;

        final byte[] gitSources = zipManager.zip(repo.getSources());
        data.setSources(gitSources);

        job.setSources(repo.getSources());
    }

    boolean hasBuild(ICIMetadata metadata){
        final CIProject job = (CIProject) jenkins.read(metadata);
        return nonNull(job.getBuild());
    }

    void setGitFolder(File dir){
        ((GitAbstractStorage) git.getGit().getStorage()).setBaseRepository(dir);
    }

    @Autowired
    FileManager files;
    @Autowired
    ZipArchiveManager zipManager;
    @Autowired
    MySqlDatabaseManager mySqlManager;

    /* TEMP PART */
    //TODO: remove after support of common-data

    @Autowired
    ProjectDataRepository dataRepository;

    void applyFix() {
        final ProjectData data = new ProjectData();
        data.setSources(metadata.getProjectSources());
        data.setName(metadata.getArtifactId());
        dataRepository.save(data);
    }

    void applyFix(byte[] sources){
        final ProjectData data = dataRepository
                .findByName(metadata.getArtifactId())
                .orElseThrow(RuntimeCryptoException::new);

        data.setSources(sources);

        dataRepository.save(data);
    }
}
