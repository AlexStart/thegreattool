package com.sam.jcc.cloud.ci.impl;

import com.offbytwo.jenkins.JenkinsServer;
import com.sam.jcc.cloud.ci.CIProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

import java.net.URI;

import static com.sam.jcc.cloud.ci.CIBuildStatus.IN_PROGRESS;
import static java.lang.Thread.sleep;

/**
 * @author Alexey Zhytnik
 * @since 23-Dec-16
 */
public abstract class JenkinsBaseTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    protected Jenkins jenkins;

    @Before
    public final void startUp() throws Exception {
        jenkins = new Jenkins(getJenkins(), temp.newFolder());
        setUp();
    }

    public void setUp() throws Exception {
    }

    private JenkinsServer getJenkins() throws Exception {
        setUpPluginRepository();
        final URI uri = jenkinsRule.getURL().toURI();
        return new JenkinsServer(uri);
    }

    /**
     * Test Jenkins Server has only limited local plugin repository.
     * Adds Jenkins Central plugin repository.
     */
    private void setUpPluginRepository() {
        new JenkinsUpdateCenterManager(jenkinsRule.getInstance()).addJenkinsCentral();
    }

    protected final void waitWhileProcessing(CIProject project) throws Exception {
        while (jenkins.getLastBuildStatus(project) == IN_PROGRESS) sleep(100L);
    }
}
