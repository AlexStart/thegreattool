package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import org.junit.ClassRule;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

public class GitlabVCS1Test extends AbstractVCSTest {

    @ClassRule
    public static DockerComposeContainer gitlab = new DockerComposeContainer(
            new File("../../docker/v2/docker-compose.yml"));


    public GitlabVCS1Test() {
        super(new GitlabServerVCS(new InitOnceAdminBean()));
    }
}
