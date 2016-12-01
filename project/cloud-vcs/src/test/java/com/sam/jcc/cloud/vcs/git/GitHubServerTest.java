package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.vcs.AbstractServerTest;
import org.junit.Test;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public class GitHubServerTest extends AbstractServerTest<GitHubServer> {

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