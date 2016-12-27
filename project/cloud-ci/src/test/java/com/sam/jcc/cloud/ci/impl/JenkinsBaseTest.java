package com.sam.jcc.cloud.ci.impl;

import com.offbytwo.jenkins.JenkinsServer;
import com.sam.jcc.cloud.ci.CIProject;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.sam.jcc.cloud.ci.CIBuildStatus.IN_PROGRESS;
import static java.lang.Thread.sleep;
import static java.util.logging.Logger.GLOBAL_LOGGER_NAME;

/**
 * @author Alexey Zhytnik
 * @since 23-Dec-16
 */
@Slf4j
public abstract class JenkinsBaseTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @ClassRule
    public static TemporaryFolder temp = new TemporaryFolder();

    protected static Jenkins jenkins;

    static {
        disableJenkinsUglyLogging();
    }

    @BeforeClass
    public static void startUpJenkins() throws Exception {
        log.info("Jenkins started");
        jenkins = new Jenkins(getJenkins(), temp.newFolder());
        log.info("Jenkins fully configured");
    }

    protected final void waitWhileProcessing(CIProject project) throws Exception {
        log.info("Start wait {}", project);
        log.info("Waiting for {}", project);
        while (jenkins.getLastBuildStatus(project) == IN_PROGRESS) sleep(100L);
        log.info("{} finished", project);
    }

    /**
     * Sometimes Jenkins can be in uncoordinated state, fix it possible problem.
     */
    protected final void deleteProject(CIProject project) throws Exception {
        Thread.sleep(2_000L);
        jenkins.delete(project);
    }

    private static JenkinsServer getJenkins() throws Exception {
        setUpPluginRepository();
        final URI uri = jenkinsRule.getURL().toURI();
        return new JenkinsServer(uri);
    }

    /**
     * Test Jenkins Server has only limited local plugin repository.
     * Adds Jenkins Central plugin repository.
     */
    private static void setUpPluginRepository() {
        new JenkinsUpdateCenterManager(jenkinsRule.getInstance()).addJenkinsCentral();
    }

    private static void disableJenkinsUglyLogging() {
        LogManager.getLogManager().reset();

        final Logger global = Logger.getLogger(GLOBAL_LOGGER_NAME);
        global.setLevel(Level.OFF);
    }
}
