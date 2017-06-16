package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.VCSRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper.repository;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Ignore
public class GitHubVCSTest {

    protected GitHubServerVCS server;

    protected VCSRepository repository = repository();

    @Before
    public void setUp() {
        server = new GitHubServerVCS();
        //TODO: set user, password, access token
    }

    @Test
    public void worksWithHttps() {
        server.setProtocol("https");
    }

    @Test
    public void createsAndChecksExistence() throws Exception {
        assertThat(server.isExist(repository)).isFalse();

        server.create(repository);
        sleep(2000L);

        assertThat(server.isExist(repository)).isTrue();
    }

    @After
    public void tearDown() {
        server.delete(repository);
    }
}