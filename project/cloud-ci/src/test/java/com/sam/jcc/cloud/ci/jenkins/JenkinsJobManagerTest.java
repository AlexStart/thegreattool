package com.sam.jcc.cloud.ci.jenkins;

import com.offbytwo.jenkins.model.JobWithDetails;
import com.sam.jcc.cloud.ci.CIBuildStatus;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.exception.CIProjectNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.sam.jcc.cloud.ci.CIBuildStatus.UNKNOWN;
import static com.sam.jcc.cloud.ci.util.CIProjectTemplates.loadProject;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 21-Dec-16
 */
public class JenkinsJobManagerTest extends JenkinsBaseTest {

    CIProject project;
    JenkinsJobManager jobManager;

    @Before
    public void setUp() throws Exception {
        jobManager = new JenkinsJobManager(jenkins.getServer());

        project = loadProject("maven", temp.newFolder());
        jenkins.create(project);
    }

    @After
    public void tearDown() {
        jenkins.delete(project);
    }

    @Test
    public void loadsJob() {
        final JobWithDetails job = jobManager.loadJob(project);
        assertThat(job).isNotNull();
    }

    @Test
    public void knowsJobExist() throws Exception {
        assertThat(jobManager.hasJob(project)).isTrue();
        assertThat(jobManager.hasJob(unknownProject())).isFalse();
    }

    @Test
    public void getsBuildStatus() {
        final CIBuildStatus status = jobManager.getBuildStatus(project);
        assertThat(status).isEqualTo(UNKNOWN);
    }

    @Test(expected = CIProjectNotFoundException.class)
    public void failsOnJobLoadUnknown() throws Exception {
        jobManager.loadJob(unknownProject());
    }

    @Test(expected = CIProjectNotFoundException.class)
    public void failsOgGetBuildOfUnknown() throws Exception {
        jobManager.getBuildStatus(unknownProject());
    }

    CIProject unknownProject() throws Exception {
        final CIProject project = loadProject("maven", temp.newFolder());
        project.setArtifactId("unknown");
        return project;
    }
}