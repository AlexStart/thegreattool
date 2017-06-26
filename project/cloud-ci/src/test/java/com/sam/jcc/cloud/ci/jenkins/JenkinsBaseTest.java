package com.sam.jcc.cloud.ci.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JenkinsBaseTest.JenkinsTestConfig.class})
public abstract class JenkinsBaseTest {

    @Autowired
    ApplicationContext context;

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @ClassRule
    public static TemporaryFolder temp = new TemporaryFolder();

    protected static final long JOB_TIMEOUT = 200_000L;

    protected static Jenkins jenkins;
    private static JenkinsServer jenkinsServer;

    static {
        disableJenkinsUglyLogging();
    }

    @BeforeClass
    public static void startUpJenkins() throws Exception {
        log.info("Jenkins started");
        jenkinsServer = initJenkinsServer();
        log.info("Jenkins fully configured");
    }

    @PostConstruct
    public void cleanUpJenkins() throws IOException {
        jenkins = context.getBean(Jenkins.class, jenkinsServer, temp.newFolder());
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

        //TODO(a bad part of the app): the sleep should be changed by something smart checking
        sleep(3_000L /* timeout for Jenkins stabilization */);
    }

    /**
     * Sometimes Jenkins does some actions with CIProject and can't immediately delete Job.
     */
    protected final void deleteQuietly(CIProject project) throws Exception {
        //TODO(a bad part of the app): after getting logs of failed build you can't immediately delete job
        sleep(1_500L);
        jenkins.delete(project);
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

    @Configuration
    @ComponentScan("com.sam.jcc.cloud.ci")
    public static class JenkinsTestConfig {

        @Bean
        public ProjectDataRepository projectDataRepository() {
            return Mockito.mock(ProjectDataRepository.class);
        }

        @Bean
        public List<IEventManager<ICIMetadata>> eventManagers() {
            return Collections.emptyList();
        }

        @Bean
        public InitOnceAdminBean initOnceAdminBean() {
            return new InitOnceAdminBean();
        }

    }

}
