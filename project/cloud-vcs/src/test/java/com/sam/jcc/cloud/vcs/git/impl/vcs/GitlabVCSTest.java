package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab.CreateCommitCommand;
import com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab.GetVersionCommand;
import com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab.GitlabVersion;
import org.gitlab.api.models.GitlabCommit;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import static com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper.notEmptyRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

@IfProfileValue(name = "spring.profiles.active", value = "testcontainers")
@RunWith(SpringRunner.class) //TODO: remove with testcontainers 1.3.1 release - used for @IfProfileValue
public class GitlabVCSTest extends AbstractVCSTest {

    //TODO: wait for testcontainers 1.3.1 release - expected the next bugs to eb fixed:
    //TODO: https://github.com/testcontainers/testcontainers-java/pull/358
    //TODO: https://github.com/testcontainers/testcontainers-java/issues/342
    @ClassRule
    public static DockerComposeContainer gitlab = new DockerComposeContainer(
            new File("src/test/resources/docker-compose.yml"))
            .withEnv("api.version", "1.23")
            .withExposedService("javacloud-gitlab-test", 18083)
            .withExposedService("javacloud-gitlab-test", 18322);

    private final GitlabServerVCS vcs;

    private final VCSRepository notEmptyRepository = notEmptyRepository();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    public GitlabVCSTest() {
        super(new GitlabServerVCS(
                new InitOnceAdminBean(), new CreateCommitCommand(), new GetVersionCommand()));
        this.vcs = (GitlabServerVCS) super.vcs;
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
            failBecauseExceptionWasNotThrown(VCSException.class);
        } catch (VCSException ex) {
            assertThat(ex.getMessage()).isEqualTo("Version Control System Error");
            assertThat(ex.getCause()).isNotNull();
            assertThat(ex.getCause().getMessage()).contains("401 Unauthorized");
        } finally {
            //Set old password back
            vcs.updateCurrentUserPassword(oldPassword);
        }
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

    @Test
    public void getVersion() throws Exception {
        GitlabVersion version = vcs.getVersion();

        assertThat(version).isNotNull();
        assertThat(version.getVersion()).isNotEmpty();
        assertThat(version.getRevision()).isNotEmpty();
    }

    @Test
    public void getApiUrl() throws Exception {
        URL apiURL = vcs.getApiUrl();

        assertThat(apiURL).isNotNull();
        assertThat(apiURL.toString()).startsWith("http://");
    }
}