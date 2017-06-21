package com.sam.jcc.cloud.providers;

import com.sam.jcc.cloud.dataprovider.impl.MongoDataProvider;
import com.sam.jcc.cloud.dataprovider.impl.MongoDatabaseManager;
import com.sam.jcc.cloud.project.impl.GradleProjectProvider;
import com.sam.jcc.cloud.util.TestEnvironment;
import com.sam.jcc.cloud.vcs.git.impl.provider.GitlabProvider;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@IfProfileValue(name = "spring.profiles.active", value = "testcontainers")
public class GradleMongoGitlabJenkinsProvidersIntegrationTest extends AbstractProvidersIntegrationTest {

    //TODO: wait for testcontainers 1.3.1 release - expected the next bugs to eb fixed:
    //TODO: https://github.com/testcontainers/testcontainers-java/pull/358
    //TODO: https://github.com/testcontainers/testcontainers-java/issues/342
    @ClassRule
    public static DockerComposeContainer gitlabContainer = new DockerComposeContainer(
            new File("src/test/resources/docker-compose.yml"))
            .withEnv("api.version", "1.23")
            .withExposedService("javacloud-gitlab-test", 18083)
            .withExposedService("javacloud-gitlab-test", 18322);

    private static final String GRADLE_PROJECT = "gradle-project";

    @Autowired
    GradleProjectProvider gradleGenerator;

    @Autowired
    MongoDataProvider mongoInjector;

    @Autowired
    MongoDatabaseManager mongoManager;

    @Autowired
    GitlabProvider gitlab;

    @BeforeClass
    public static void startUpEnv() throws Exception {
        startUpJenkins();
    }

    @Before
    public void setUp() throws IOException {
        job = job();
        app = app(GRADLE_PROJECT);
        data = data(app);
        metadata = project(data, GRADLE_PROJECT);
        repository = repository();

        mongoManager.drop(data);
        apps.findAll().forEach(apps::delete);

        jenkins.setJenkins(TestEnvironment.jenkins);
    }

    @After
    public void tearDown() {
        mongoManager.drop(data);
    }

    @Test
    public void createsAndGeneratesWithGradleAndInjectsMongoAndStoresInGitlabAndBuildOnJenkins() throws Exception {
        apps.create(app);

        final byte[] sources = readSources(gradleGenerator.update(metadata));
        assertThat(sources).isNotEmpty();

        loadAndCopySourcesTo(job, data, repository);
        gitlab.create(repository);

        mongoInjector.update(data);
        assertThat(data.getSources()).isNotEqualTo(sources);

        loadAndCopySourcesTo(repository);
        gitlab.update(repository);

        clearLocalSources(repository);
        copySourcesTo(gitlab.read(repository), job, data);

        jenkins.create(job);
        waitWhileProcessing(job);
        //TODO: uncommit after jenkins and gitlab integration
        //assertThat(getBuild(jenkins.read(job))).isNotEmpty();

        deleteQuietly(job);
        gitlab.delete(repository);
        apps.delete(app);
    }
}
