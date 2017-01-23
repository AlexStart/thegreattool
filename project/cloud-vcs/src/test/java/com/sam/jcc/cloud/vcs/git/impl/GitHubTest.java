package com.sam.jcc.cloud.vcs.git.impl;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Ignore
public class GitHubTest extends AbstractVCSStorageTest<GitHubServer> {

    @Override
    public void setUp() {
        server = new GitHubServer();
        //TODO: set user, password, access token
    }

    @Test
    public void worksWithHttps() {
        server.setProtocol("https");
    }
}