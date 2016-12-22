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
import java.text.MessageFormat;

import static com.sam.jcc.cloud.ci.impl.JenkinsConfigurationBuilder.MAVEN_ARTIFACTS;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author Alexey Zhytnik
 * @since 18-Dec-16
 */
public class JenkinsConfigurationBuilderTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    CIProject mavenProject;
    CIProject gradleProject;

    ItemStorage<CIProject> workspace;
    JenkinsConfigurationBuilder builder;

    @Before
    public void setUp() throws Exception {
        workspace = spy(new ItemStorage<>(CIProject::getName));
        workspace.setRoot(temp.newFolder());

        mavenProject = project("maven");
        gradleProject = project("gradle");

        builder = new JenkinsConfigurationBuilder(workspace);
    }

    @Test
    public void configures() {
        assertThat(builder.build(mavenProject))
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    public void configuresMavenForWindows() {
        if (IS_OS_WINDOWS) {
            assertThat(builder.build(mavenProject)).contains("mvnw.cmd install");
            assertThat(builder.build(gradleProject)).contains("gradlew.bat build");
        }
    }

    @Test
    public void putsPathToProjectRepositoryLocation() {
        final String expectedPath = workspace.get(mavenProject).getAbsolutePath();
        assertThat(builder.build(mavenProject)).contains(expectedPath);
    }

    @Test
    public void putsPathToArtifacts() {
        assertThat(builder.build(mavenProject)).contains(MAVEN_ARTIFACTS);
    }

    @Test
    public void getsSourcesFromWorkspace() {
        builder.build(mavenProject);
        verify(workspace, atLeastOnce()).get(mavenProject);
    }

    @Test
    public void knowsProjectTypes() {
        assertThat(builder.isMaven(workspace.get(mavenProject))).isTrue();
        assertThat(builder.isMaven(workspace.get(gradleProject))).isFalse();
    }

    CIProject project(String type) throws Exception {
        final CIProject p = new CIProject();
        p.setArtifactId("Project-" + type);

        final String path = MessageFormat.format("/{0}-project.zip", type);
        copyProjectSourcesIntoFolder(path, workspace.create(p));
        return p;
    }

    void copyProjectSourcesIntoFolder(String project, File dir) throws Exception {
        final File sources = new ClassPathResource(project).getFile();
        new ZipArchiveManager().unzip(sources, dir);
    }
}