package com.sam.jcc.cloud.providers;

import com.sam.jcc.cloud.app.AppMetadata;
import com.sam.jcc.cloud.app.AppProvider;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.impl.JenkinsProvider;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.util.Collections.singletonList;

/**
 * @author Alexey Zhytnik
 * @since 19-Jan-17
 */
public class AbstractProvidersIntegrationTest extends TestEnvironment {

    final String PROJECT_ARTIFACT_ID = "iproject" + new Random().nextInt(1000);
    final String PROJECT_GROUP_ID = "com.samsolutions";

    @Autowired
    protected AppProvider apps;

    @Autowired
    protected JenkinsProvider jenkins;

    protected AppMetadata app;

    protected AppData data;
    protected CIProject job;
    protected ProjectMetadata metadata;
    protected VCSRepository repository;

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
        final CIProject job = (CIProject) jenkins.read(metadata);
        return job.getBuild();
    }
}
