package com.sam.jcc.cloud.providers;

import com.sam.jcc.cloud.app.AppMetadata;
import com.sam.jcc.cloud.app.AppProvider;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.jenkins.JenkinsProvider;
import com.sam.jcc.cloud.ci.jenkins.Jenkins;
import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import com.sam.jcc.cloud.project.ProjectMetadata;
import com.sam.jcc.cloud.util.TestEnvironment;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static com.sam.jcc.cloud.ci.CIBuildStatus.IN_PROGRESS;
import static java.lang.Thread.sleep;
import static java.util.Collections.singletonList;

/**
 * @author Alexey Zhytnik
 * @since 19-Jan-17
 */
@Slf4j
public class AbstractProvidersIntegrationTest extends TestEnvironment {

    final String PROJECT_ARTIFACT_ID = "iproject" + new Random().nextInt(1000);
    final String PROJECT_GROUP_ID = "com.samsolutions";

    @Autowired
    ApplicationContext context;

    @Autowired
    protected AppProvider apps;
    @Autowired
    protected JenkinsProvider jenkinsProvider;

    protected AppMetadata app;

    protected AppData data;
    protected CIProject job;
    protected ProjectMetadata metadata;
    protected VCSRepository repository;
    protected Jenkins jenkins; //TODO[rfisenko 6/16/17]: bad solution. make refactoring. Move to TestEnvironment.class

    AppMetadata app(String projectType) {
        final AppMetadata app = new AppMetadata();
        app.setProjectName(PROJECT_ARTIFACT_ID);
        app.setType(projectType);
        return app;
    }

    ProjectMetadata project(AppData appData, String projectType) {
        final ProjectMetadata metadata = new ProjectMetadata();

        metadata.setJavaVersion("1.8");
        metadata.setGroupId(PROJECT_GROUP_ID);
        metadata.setArtifactId(appData.getAppName());
        metadata.setProjectType(projectType);
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
    ProjectDataRepository dataRepository;

    byte[] readSources(IProjectMetadata metadata) {
        return metadata.getProjectSources();
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
        final CIProject job = (CIProject) jenkinsProvider.read(metadata);
        return job.getBuild();
    }

    protected void waitWhileProcessing(CIProject project) throws Exception {
        log.info("Start wait {}", project);
        log.info("Waiting for {}", project);

        long timeOut = JOB_TIMEOUT;

        while (jenkins.getLastBuildStatus(project) == IN_PROGRESS && timeOut > 0L) {
            sleep(500L);
            timeOut -= 500L;
        }

        if (timeOut <= 0L) {
            throw new RuntimeException("TimeOut");
        }
        log.info("{} finished", project);

        sleep(1_000L /* timeout for Jenkins stabilization */);
    }

    /**
     * Sometimes Jenkins does some actions with CIProject and can't immediately delete Job.
     */
    protected void deleteQuietly(CIProject project) throws Exception {
        sleep(1_500L);
        jenkinsProvider.delete(project);
    }
}
