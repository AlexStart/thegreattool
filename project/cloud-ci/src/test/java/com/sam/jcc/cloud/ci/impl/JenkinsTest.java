package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIProjectAlreadyExistsException;
import com.sam.jcc.cloud.ci.exception.CIProjectNotFoundException;
import org.junit.Test;

import static com.sam.jcc.cloud.ci.CIBuildStatus.FAILED;
import static com.sam.jcc.cloud.ci.CIBuildStatus.SUCCESSFUL;
import static com.sam.jcc.cloud.ci.util.CIProjectTemplates.loadProject;
import static com.sam.jcc.cloud.ci.util.CIProjectTemplates.projectWithFailedTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
public class JenkinsTest extends JenkinsBaseTest {

    CIProject project;

    public void setUp() throws Exception {
        project = loadProject("maven", temp.newFolder());
    }

    @Test
    public void creates() {
        jenkins.create(project);
        jenkins.delete(project);
    }

    @Test
    public void checksExistence() {
        jenkins.create(project);

        try {
            jenkins.create(project);
            fail("should check existence");
        } catch (CIProjectAlreadyExistsException expected) {}
        finally {
            jenkins.delete(project);
        }
    }

    @Test(timeout = 45_000L)
    public void buildsMavenProject() throws Exception {
        buildProject(project);
    }

    @Test(timeout = 45_000L)
    public void buildsGradleProject() throws Exception {
        final CIProject gradleProject = loadProject("gradle", temp.newFolder());
        buildProject(gradleProject);
    }

    void buildProject(CIProject project) throws Exception {
        jenkins.create(project);

        jenkins.build(project);
        waitWhileProcessing(project);

        assertThat(jenkins.getLastBuildStatus(project)).isEqualTo(SUCCESSFUL);

        assertThat(jenkins.getLastSuccessfulBuild(project)).isNotNull();
        jenkins.delete(project);
    }

    @Test(expected = CIBuildNotFoundException.class, timeout = 45_000L)
    public void failsOnGetFailedBuild() throws Exception {
        final CIProject project = projectWithFailedTest(temp.newFolder());

        jenkins.create(project);
        jenkins.build(project);
        waitWhileProcessing(project);

        assertThat(jenkins.getLastBuildStatus(project)).isEqualTo(FAILED);

        try{
            jenkins.getLastSuccessfulBuild(project);
            fail("Should not return failed build");
        } finally {
            jenkins.delete(project);
        }
    }

    @Test(expected = CIProjectNotFoundException.class)
    public void failsOnDeleteUnknown() {
        jenkins.delete(project);
    }

    @Test(expected = CIProjectNotFoundException.class)
    public void failsOnBuildUnknown() {
        jenkins.build(project);
    }

    @Test(expected = CIProjectNotFoundException.class)
    public void failsOnGetBuildOfUnknown() {
        jenkins.getLastSuccessfulBuild(project);
    }

    @Test(expected = CIProjectNotFoundException.class)
    public void failsOnGetStatusOfUnknown() {
        jenkins.getLastBuildStatus(project);
    }
}
