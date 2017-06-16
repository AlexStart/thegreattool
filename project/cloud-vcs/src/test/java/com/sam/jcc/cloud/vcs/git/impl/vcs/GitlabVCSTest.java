package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import org.assertj.core.api.Java6Assertions;
import org.gitlab.api.models.GitlabCommit;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.GenericContainer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper.notEmptyRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.wait.Wait.forHttp;

public class GitLabVCSTest extends AbstractVCSTest {

    //TODO maybe set fixed image version: latest to 9.2.5-ce.0
    @ClassRule
    public static GenericContainer gitlab =
            new GenericContainer("gitlab/gitlab-ce:latest")
                    .withExposedPorts(8083, 8322)
                    .withEnv("GITLAB_OMNIBUS_CONFIG", "|\n" +
                            "        external_url \"http://#{host}/gitlab\"\n" +
                            "        nginx['listen_port'] = 8083\n" +
                            "        gitlab_rails['gitlab_shell_ssh_port'] = 8322")
                    .withEnv("GITLAB_ROOT_PASSWORD", "rootpassword")
                    .withEnv("GITLAB_HOST", "localhost")
                    .waitingFor(forHttp("/gitlab")
                            .forStatusCode(200)
                            .usingTls());

    private final GitLabServerVCS vcs;

    private final VCSRepository notEmptyRepository = notEmptyRepository();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    public GitLabVCSTest() {
        super(new GitLabServerVCS());
        this.vcs = (GitLabServerVCS) super.vcs;
    }

    @Before
    public void setUp() throws IOException {
        setTemp(temp);
    }

    @After
    public void tearDown() {
        if (vcs.isExist(repository)) {
            vcs.delete(repository);
        }
        if (vcs.isExist(notEmptyRepository)) {
            vcs.delete(notEmptyRepository);
        }
    }

    @Test(expected = VCSException.class)
    public void failsOnCreationExistence() {
        vcs.create(repository);
        vcs.create(repository);
    }

    @Test
    public void setHttpProtocolSuccessfully() throws Exception {
        vcs.setProtocol("http");
    }

    @Test(expected = VCSUnknownProtocolException.class)
    public void setNonHttpProtocolFailed() throws Exception {
        vcs.setProtocol("ssh");
    }

    @Test
    public void isEnabled() {
        assertThat(vcs.isEnabled()).isTrue();
    }

    @Test
    public void updateCurrentUserPassword() {
        String user = vcs.getUser();
        String oldPassword = vcs.getPassword();
        vcs.getToken(user, oldPassword);

        String newPassword = "newPassword";
        vcs.updateCurrentUserPassword(newPassword);
        vcs.getToken(user, newPassword);

        try {
            vcs.getToken(user, oldPassword);
        } catch (VCSException ex) {
            Java6Assertions.assertThat(ex.getMessage()).isEqualTo("Version Control System Error");
            Java6Assertions.assertThat(ex.getCause()).isNotNull();
            Java6Assertions.assertThat(ex.getCause().getMessage()).contains("401 Unauthorized");
        }

        //Set old password back
        vcs.updateCurrentUserPassword(oldPassword);
        vcs.getToken(user, oldPassword);
    }

    @Test
    public void commitRead() throws Exception {
        vcs.create(repository);
        final File dest = temp.newFolder();
        repository.setSources(dest);

        final File src = temp.newFolder();
        final File file = new File(src, new Random().nextInt() + "-file.txt");
        String content = writeRandomStringToFile(file);
        repository.setSources(src);
        vcs.commit(repository);
        List<GitlabCommit> commits = vcs.getAllCommits(repository);
        assertThat(commits != null).isTrue();
        assertThat(commits.size()).isEqualTo(1);
        assertThat(commits.get(0).getMessage()).isEqualTo(repository.getCommitMessage());

        repository.setSources(dest);
        vcs.read(repository);

        assertThat(dest.listFiles()).isNotNull().isNotEmpty();
        final File copy = new File(dest, file.getName());
        assertThat(copy).exists().isFile().hasContent(content);
    }

    @Test
    public void getAllRepositories() throws Exception {
        vcs.create(repository);
        vcs.create(notEmptyRepository);

        List<VCSRepository> repos = vcs.getAllRepositories();

        assertThat(repos != null).isTrue();
        assertThat(repos.size()).isEqualTo(2);
        assertThat(repos.get(0).getArtifactId()).startsWith("temp");
        assertThat(repos.get(1).getArtifactId()).startsWith("temp");
    }
}