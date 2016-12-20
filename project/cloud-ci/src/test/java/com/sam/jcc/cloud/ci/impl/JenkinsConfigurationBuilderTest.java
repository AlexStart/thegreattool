package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static com.sam.jcc.cloud.ci.impl.JenkinsConfigurationBuilder.MAVEN_ARTIFACTS;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Alexey Zhytnik
 * @since 18-Dec-16
 */
public class JenkinsConfigurationBuilderTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    CIProject project;

    ItemStorage<CIProject> workspace;
    JenkinsConfigurationBuilder builder;

    @Before
    public void setUp() throws Exception {
        workspace = spy(new ItemStorage<>(CIProject::getName));
        workspace.setRoot(temp.newFolder());

        project = new CIProject();
        project.setArtifactId("TempProject");
        copyMavenProjectSourcesIntoStorage(workspace.create(project));

        builder = new JenkinsConfigurationBuilder(workspace);
    }

    @Test
    public void configures() {
        assertThat(builder.build(project))
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    public void configuresMavenForWindows() {
        if (IS_OS_WINDOWS) assertThat(builder.build(project)).contains("mvnw.cmd install");
    }

    @Test
    public void putsPathToProjectRepositoryLocation() {
        final String expectedPath = workspace.get(project).getAbsolutePath();
        assertThat(builder.build(project)).contains(expectedPath);
    }

    @Test
    public void putsPathToArtifacts() {
        assertThat(builder.build(project)).contains(MAVEN_ARTIFACTS);
    }

    @Test
    public void getsSourcesFromWorkspace() {
        builder.build(project);
        verify(workspace, atLeastOnce()).get(project);
    }

    void copyMavenProjectSourcesIntoStorage(File dir) throws Exception {
        final File sources = new ClassPathResource("/maven-project.zip").getFile();
        new ZipArchiveManager().unzip(sources, dir);
    }
}