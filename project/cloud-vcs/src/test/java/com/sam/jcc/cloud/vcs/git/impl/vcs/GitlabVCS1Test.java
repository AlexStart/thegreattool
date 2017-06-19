package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import org.junit.ClassRule;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

public class GitlabVCS1Test extends AbstractVCSTest {

    @ClassRule
    public static DockerComposeContainer gitlab = new DockerComposeContainer(
            new File("src/test/resources/docker-compose.yml"))
            .withExposedService("gitlab", 18083);

    public GitlabVCS1Test() {
        super(new GitlabServerVCS(new InitOnceAdminBean()));
    }
}
