package com.sam.jcc.cloud.providers;

import com.sam.jcc.cloud.dataprovider.impl.MySqlDataProvider;
import com.sam.jcc.cloud.dataprovider.impl.MySqlDatabaseManager;
import com.sam.jcc.cloud.project.impl.MavenProjectProvider;
import com.sam.jcc.cloud.util.GitDaemon;
import com.sam.jcc.cloud.util.TestEnvironment;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.git.impl.provider.GitProtocolProvider;
import com.sam.jcc.cloud.vcs.git.impl.vcs.GitRemoteVCS;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 19-Jan-17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MavenMysqlGitJenkinsProvidersIntegrationTest extends AbstractProvidersIntegrationTest {

    private static final String MAVEN_PROJECT = "maven-project";

    @Autowired
    MavenProjectProvider mavenGenerator;

    @Autowired
    MySqlDataProvider mySqlInjector;

    @Autowired
    MySqlDatabaseManager mySqlManager;

    @Autowired
    GitProtocolProvider git;

    private static GitDaemon daemon;

    @BeforeClass
    public static void startUpEnv() throws Exception {
        startUpJenkins();

        daemon = new GitDaemon();
        daemon.startUp(temp.newFolder());
    }

    @AfterClass
    public static void shutDownGitDaemon() {
        daemon.shutDown();
    }

    @Before
    public void setUp() throws IOException {
        job = job();
        app = app(MAVEN_PROJECT);
        data = data(app);
        metadata = project(data, MAVEN_PROJECT);
        repository = repository();

        mySqlManager.drop(data);
        apps.findAll().forEach(apps::delete);

        jenkins.setJenkins(TestEnvironment.jenkins);
        setUpGitRemoteStorage(daemon);
    }

    @After
    public void tearDown() {
        mySqlManager.drop(data);
    }

    @Test
    public void createsAndGeneratesAndInjectsMySqlAndBuildsOnJenkins() throws Exception {
        apps.create(app);

        final byte[] sources = readSources(mavenGenerator.update(metadata));
        assertThat(sources).isNotEmpty();

        loadAndCopySourcesTo(job, data, repository);
        git.create(repository);

        mySqlInjector.update(data);
        assertThat(data.getSources()).isNotEqualTo(sources);

        loadAndCopySourcesTo(repository);
        git.update(repository);

        clearLocalSources(repository);
        copySourcesTo(git.read(repository), job, data);

        jenkins.create(job);
        waitWhileProcessing(job);
        assertThat(getBuild(jenkins.read(job))).isNotEmpty();

        deleteQuietly(job);
        disableGitSupport(repository);
        apps.delete(app);
    }

    void setUpGitRemoteStorage(GitDaemon daemon) {
        final GitRemoteVCS vcs = (GitRemoteVCS) git.getVcs();
        vcs.setBaseRepository(daemon.getStorage());
        vcs.setPort(daemon.getCurrentPort());
    }

    void disableGitSupport(VCSRepository repo) throws IOException {
        daemon.disableExport(repo);
        git.delete(repo);
    }
}
