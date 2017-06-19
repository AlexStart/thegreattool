package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import org.junit.ClassRule;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;

import java.io.File;

import static org.testcontainers.containers.wait.Wait.forHttp;

public class GitlabVCS2Test extends AbstractVCSTest {

    @ClassRule
    public static GenericContainer gitlab =
            new GenericContainer("gitlab/gitlab-ce:latest")
                    .withExposedPorts(8083, 8322)
                    .withEnv("GITLAB_OMNIBUS_CONFIG", "|\n" +
                            "        external_url \"http://#{host}/gitlab\"\n" +
                            "        nginx['listen_port'] = 8083\n" +
                            "        gitlab_rails['gitlab_shell_ssh_port'] = 8322")
                    .withEnv("GITLAB_ROOT_PASSWORD", "rootpassword");

    public GitlabVCS2Test() {
        super(new GitlabServerVCS(new InitOnceAdminBean()));
    }
}
