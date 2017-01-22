package com.sam.jcc.cloud.vcs.git.impl;

import org.junit.Ignore;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Ignore
public class GitLabTest extends AbstractVCSStorageTest<GitLabServer> {

    @Override
    public void setUp() {
        server = new GitLabServer();
        //TODO: set host, user, password
    }
}