package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIProjectAlreadyExistsException;
import com.sam.jcc.cloud.ci.exception.CIProjectNotFoundException;
import com.sam.jcc.cloud.event.DefaultLoggingEventManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.InputStream;

import static com.sam.jcc.cloud.ci.CIBuildStatus.FAILED;
import static com.sam.jcc.cloud.ci.CIBuildStatus.IN_PROGRESS;
import static com.sam.jcc.cloud.ci.CIBuildStatus.SUCCESSFUL;
import static com.sam.jcc.cloud.ci.CIProjectStatus.CONFIGURED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.CREATED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.DELETED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_NO_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.UPDATED;
import static com.sam.jcc.cloud.ci.impl.JenkinsUtil.getJenkins;
import static com.sam.jcc.cloud.ci.impl.JenkinsUtil.loadProject;
import static com.sam.jcc.cloud.ci.impl.JenkinsUtil.projectWithFailedTest;
import static java.lang.Thread.sleep;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
public class JenkinsTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    Jenkins jenkins;
    CIProject project;

    @Before
    public void setUp() throws Exception {
        jenkins = getJenkins(temp.newFolder());
        jenkins.setEventManagers(singletonList(new DefaultLoggingEventManager<>()));

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

    @Test(timeout = 120_000L)
    public void buildsMavenProject() throws Exception {
        buildProject(project);
    }

    @Test(timeout = 120_000L)
    public void buildsGradleProject() throws Exception {
        final CIProject gradleProject = loadProject("gradle", temp.newFolder());
        buildProject(gradleProject);
    }

    void buildProject(CIProject project) throws Exception {
        assertThat(project.getStatus()).isEqualTo(CONFIGURED);

        jenkins.create(project);
        assertThat(project.getStatus()).isEqualTo(CREATED);

        jenkins.build(project);
        assertThat(project.getStatus()).isEqualTo(UPDATED);

        waitWhileProcessing(project);

        assertThat(jenkins.getLastBuildStatus(project)).isEqualTo(SUCCESSFUL);

        try (InputStream build = jenkins.getLastSuccessfulBuild(project)) {
            assertThat(build).isNotNull();
        }
        assertThat(project.getStatus()).isEqualTo(HAS_BUILD);

        jenkins.delete(project);
        assertThat(project.getStatus()).isEqualTo(DELETED);
    }

    @Test(expected = CIBuildNotFoundException.class, timeout = 120_000L)
    public void failsOnGetFailedBuild() throws Exception {
        final CIProject project = projectWithFailedTest(temp.newFolder());

        jenkins.create(project);
        jenkins.build(project);
        waitWhileProcessing(project);

        assertThat(jenkins.getLastBuildStatus(project)).isEqualTo(FAILED);

        try (InputStream build = jenkins.getLastSuccessfulBuild(project)) {
            fail("Should not return failed build, but was: " + build);
        } finally {
            jenkins.delete(project);
        }
        assertThat(project.getStatus()).isEqualTo(HAS_NO_BUILD);
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

    void waitWhileProcessing(CIProject p) throws Exception {
        while (jenkins.getLastBuildStatus(p) == IN_PROGRESS) sleep(100L);
    }
}
