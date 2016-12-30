package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static com.sam.jcc.cloud.ci.impl.JenkinsConfigurationBuilder.MAVEN_ARTIFACTS;
import static com.sam.jcc.cloud.ci.util.CIProjectTemplates.loadProject;
import static com.sam.jcc.cloud.utils.SystemUtils.resetOSSettings;
import static com.sam.jcc.cloud.utils.SystemUtils.setWindowsOS;
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

        mavenProject = loadProject("maven", temp.newFolder());
        copySourcesIntoWorkspace(mavenProject);

        gradleProject = loadProject("gradle", temp.newFolder());
        copySourcesIntoWorkspace(gradleProject);

        builder = new JenkinsConfigurationBuilder(workspace);
    }

    @Test
    public void configures() {
        assertThat(builder.build(mavenProject))
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    public void configuresProjectsForDifferentOS() {
        try{
            setWindowsOS(true);
            assertThat(builder.build(mavenProject)).contains("mvnw.cmd install");
            assertThat(builder.build(gradleProject)).contains("gradlew.bat build");

            setWindowsOS(false);
            assertThat(builder.build(mavenProject)).contains("./mvnw install");
            assertThat(builder.build(gradleProject)).contains("./gradlew build");
        } finally {
            resetOSSettings();
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
        assertThat(builder.isMaven(mavenProject.getSources())).isTrue();
        assertThat(builder.isMaven(gradleProject.getSources())).isFalse();
    }

    void copySourcesIntoWorkspace(CIProject project) {
        new FileManager().copyDir(
                project.getSources(),
                workspace.create(project)
        );
    }
}