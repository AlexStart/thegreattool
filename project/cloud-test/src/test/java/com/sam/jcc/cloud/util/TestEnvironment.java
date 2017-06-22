package com.sam.jcc.cloud.util;

import com.offbytwo.jenkins.JenkinsServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

    protected static JenkinsServer jenkinsServer;

    static {
        disableJenkinsUglyLogging();
    }

    protected static void startUpJenkins() throws Exception {
        log.info("Jenkins started");
        jenkinsServer = initJenkinsServer();
        log.info("Jenkins fully configured");
    }

    private static JenkinsServer initJenkinsServer() throws Exception {
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
