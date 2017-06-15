package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.git.impl.storage.GitlabServer;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

import static org.testcontainers.containers.wait.Wait.forHttp;

public class GitlabVCSTest extends AbstractVCSTest {

    //TODO set fixed image version
    @ClassRule
    public static GenericContainer gitlab =
            new GenericContainer("gitlab/gitlab-ce:latest")
                    .withExposedPorts(8083, 8322)
                    .withEnv("GITLAB_OMNIBUS_CONFIG", "|\n" +
                            "        external_url \"http://#{host}/gitlab\"\n" +
                            "        nginx['listen_port'] = 8083\n" +
                            "        gitlab_rails['gitlab_shell_ssh_port'] = 8322")
                    .withEnv("GITLAB_ROOT_PASSWORD", "rootpassword")
                    .withEnv("GITLAB_HOST", "localhost")
                    .waitingFor(forHttp("/gitlab")
                            .forStatusCode(200)
                            .usingTls());

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    public GitlabVCSTest() {
        super(new GitServerVCS());
    }

    @Before
    public void setUp() throws IOException {
        vcs.setStorage(new GitlabServer());
        setTemp(temp);
    }

    @After
    public void tearDown() {
        if (vcs.isExist(repository)) {
            vcs.delete(repository);
        }
    }
}