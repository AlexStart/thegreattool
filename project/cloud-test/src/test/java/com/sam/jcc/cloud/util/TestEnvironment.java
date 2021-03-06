package com.sam.jcc.cloud.util;

import com.offbytwo.jenkins.JenkinsServer;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.impl.Jenkins;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
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
@SuppressWarnings("Duplicates")
public abstract class TestEnvironment {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @ClassRule
    public static TemporaryFolder temp = new TemporaryFolder();

    protected static final long JOB_TIMEOUT = 200_000L;

    protected static Jenkins jenkins;

    protected static GitDaemon daemon;

    static {
        disableJenkinsUglyLogging();
    }

    @BeforeClass
    public static void startUpEnv() throws Exception {
        log.info("Jenkins started");
        jenkins = new Jenkins(getJenkins(), temp.newFolder());
        log.info("Jenkins fully configured");

        daemon = new GitDaemon();
        daemon.startUp(temp.newFolder());
    }

    @AfterClass
    public static void shutDownGitDaemon(){
        daemon.shutDown();
    }

    protected final void waitWhileProcessing(CIProject project) throws Exception {
        log.info("Start wait {}", project);
        log.info("Waiting for {}", project);

        long timeOut = JOB_TIMEOUT;

        while (jenkins.getLastBuildStatus(project) == IN_PROGRESS && timeOut > 0L) {
            sleep(500L);
            timeOut -= 500L;
        }

        if (timeOut <= 0L) {
            throw new RuntimeException("TimeOut");
        }
        log.info("{} finished", project);

        sleep(1_000L /* timeout for Jenkins stabilization */);
    }

    /**
     * Sometimes Jenkins does some actions with CIProject and can't immediately delete Job.
     */
    protected final void deleteQuietly(CIProject project) throws Exception {
        sleep(1_500L);
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
