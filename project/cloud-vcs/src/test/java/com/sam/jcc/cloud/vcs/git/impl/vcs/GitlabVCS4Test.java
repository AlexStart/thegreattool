package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;

public class GitlabVCS4Test extends AbstractVCSTest {

    @ClassRule
    public static GenericContainer gitlab =
            new GenericContainer("gitlab/gitlab-ce:latest");

    public GitlabVCS4Test() {
        super(new GitlabServerVCS(new InitOnceAdminBean()));
    }
}
