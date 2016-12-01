package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.vcs.AbstractServerTest;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public class GitLabServerTest extends AbstractServerTest<GitLabServer> {

    @Override
    public void setUp() {
        server = new GitLabServer();
        //TODO: set host, user, password
    }
}