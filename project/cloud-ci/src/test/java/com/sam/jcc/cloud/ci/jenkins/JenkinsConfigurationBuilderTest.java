package com.sam.jcc.cloud.ci.jenkins;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.jenkins.config.JenkinsConfigurationBuilder;
import com.sam.jcc.cloud.ci.jenkins.config.vcs.GitFileVCSConfigurator;
import com.sam.jcc.cloud.ci.jenkins.config.vcs.GitProtocolVCSConfigurator;
import com.sam.jcc.cloud.ci.jenkins.config.vcs.WithoutVCSConfigurator;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import com.sam.jcc.cloud.vcs.git.impl.provider.GitFileProvider;
import com.sam.jcc.cloud.vcs.git.impl.provider.GitProtocolProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Arrays;

import static com.sam.jcc.cloud.ci.jenkins.config.JenkinsConfigurationBuilder.MAVEN_ARTIFACTS;
import static com.sam.jcc.cloud.ci.util.CIProjectTemplates.loadProject;
import static com.sam.jcc.cloud.utils.SystemUtils.resetOSSettings;
import static com.sam.jcc.cloud.utils.SystemUtils.setWindowsOS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Alexey Zhytnik
 * @since 18-Dec-16
 */
public class JenkinsConfigurationBuilderTest extends JenkinsBaseTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    CIProject mavenProject;
    CIProject gradleProject;

    ItemStorage<CIProject> workspace;
    JenkinsConfigurationBuilder builder;

    @Before
    public void setUp() throws Exception {
        workspace = spy(new ItemStorage<>(CIProject::getName, null));
        workspace.setRoot(temp.newFolder());

        mavenProject = loadProject("maven", temp.newFolder());
        copySourcesIntoWorkspace(mavenProject);

        gradleProject = loadProject("gradle", temp.newFolder());
        copySourcesIntoWorkspace(gradleProject);

        builder = new JenkinsConfigurationBuilder(workspace);
        builder.setVcsConfigurators(Arrays.asList(
                new GitFileVCSConfigurator(),
                new GitProtocolVCSConfigurator(),
                new WithoutVCSConfigurator()));
    }

    @Test
    public void configures() {
        assertThat(builder.build(mavenProject))
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    public void configuresProjectsForDifferentOS() {
        try {
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
    public void setUpVCSTest() {
        mavenProject.setVcsType(null);
        assertThat(builder.build(mavenProject).contains("hudson.scm.NullSCM"));

        mavenProject.setVcsType(GitFileProvider.TYPE);
        assertThat(builder.build(mavenProject)).contains("scm class=\"hudson.plugins.git.GitSCM\"")
                .containsPattern("<hudson.plugins.git.UserRemoteConfig>\\s*<url>\\S+</url>");

        mavenProject.setVcsType(GitProtocolProvider.TYPE);
        assertThat(builder.build(mavenProject)).contains("scm class=\"hudson.plugins.git.GitSCM\"")
                .containsPattern("<hudson.plugins.git.UserRemoteConfig>\\s*<url>\\S+</url>");
    }

    /**
     * Check that config of created job in server same as generated.
     * It need for checking that jenkins not modify configuration.
     */
    @Test
    public void gitPluginConfigTest() throws IOException {
        mavenProject.setVcsType(GitFileProvider.TYPE);
        jenkins.create(mavenProject);
        assertThat(jenkins.getServer().getJobXml(mavenProject.getName()))
                .contains("scm class=\"hudson.plugins.git.GitSCM\"")
                .containsPattern("<hudson.plugins.git.UserRemoteConfig>\\s*<url>\\S+</url>");
    }

    private void copySourcesIntoWorkspace(CIProject project) {
        new FileManager().copyDir(
                project.getSources(),
                workspace.create(project)
        );
    }
}