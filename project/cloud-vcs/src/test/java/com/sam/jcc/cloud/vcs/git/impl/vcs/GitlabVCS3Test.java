package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;

public class GitlabVCS3Test extends AbstractVCSTest {

    @ClassRule
    public static GenericContainer gitlab =
            new GenericContainer("gitlab/gitlab-ce:latest")
                    .withExposedPorts(8083, 8322);

    public GitlabVCS3Test() {
        super(new GitlabServerVCS(new InitOnceAdminBean()));
    }
}
