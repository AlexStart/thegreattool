package com.sam.jcc.cloud.vcs.git.impl.storage;

import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import org.gitlab.api.models.GitlabCommit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper.notEmptyRepository;
import static com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper.repository;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public class GitlabServerTest {

    private GitlabServer server;

    private final VCSRepository emptyRepository = repository();
    private final VCSRepository notEmptyRepository = notEmptyRepository();

    @Before
    public void setUp() {
        server = new GitlabServer();
    }

    @Test
    public void setHttpProtocolSuccessfully() throws Exception {
        server.setProtocol("http");
    }

    @Test(expected = VCSUnknownProtocolException.class)
    public void setNonHttpProtocolFailed() throws Exception {
        server.setProtocol("ssh");
    }

    @Test
    public void commit() throws Exception {
        assertThat(server.isExist(notEmptyRepository)).isFalse();
        server.create(notEmptyRepository);

        server.commit(notEmptyRepository);

        List<GitlabCommit> commits = server.getAllCommits(notEmptyRepository);
        assertThat(commits != null).isTrue();
        assertThat(commits.size()).isEqualTo(1);
        assertThat(commits.get(0).getTitle()).isEqualTo(notEmptyRepository.getCommitMessage());
        assertThat(commits.get(0).getMessage()).isEqualTo(notEmptyRepository.getCommitMessage());
    }

    @Test
    public void getAllRepositories() throws Exception {
        server.create(emptyRepository);
        server.create(notEmptyRepository);

        List<VCSRepository> repos = server.getAllRepositories();

        assertThat(repos != null).isTrue();
        assertThat(repos.size()).isEqualTo(2);
        assertThat(repos.get(0).getArtifactId()).startsWith("temp");
        assertThat(repos.get(1).getArtifactId()).startsWith("temp");
    }

    @Test
    public void isEnabled() {
        assertThat(server.isEnabled()).isTrue();
    }

    @Test
    public void updateCurrentUserPassword() {
        String user = server.getUser();
        String oldPassword = server.getPassword();
        server.getToken(user, oldPassword);

        String newPassword = "newPassword";
        server.updateCurrentUserPassword(newPassword);
        server.getToken(user, newPassword);

        try {
            server.getToken(user, oldPassword);
        } catch (VCSException ex) {
            assertThat(ex.getMessage()).isEqualTo("Version Control System Error");
            assertThat(ex.getCause()).isNotNull();
            assertThat(ex.getCause().getMessage()).contains("401 Unauthorized");
        }

        //Set old password back
        server.updateCurrentUserPassword(oldPassword);
        server.getToken(user, oldPassword);
    }

    @After
    public void clearDown() throws InterruptedException {
        if (server.isExist(emptyRepository)) {
            server.delete(emptyRepository);
        }
        if (server.isExist(notEmptyRepository)) {
            server.delete(notEmptyRepository);
        }
//        //TODO don't like sleep here
//        sleep(2000);
    }
}