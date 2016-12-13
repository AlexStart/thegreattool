package com.sam.jcc.cloud.vcs.git;

import org.junit.Ignore;
import org.junit.Test;

import com.sam.jcc.cloud.vcs.AbstractVCSStorageTest;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Ignore // TODO  Issue #8 Fix GitHubTest with newer versions of Jackson
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