import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import com.sam.jcc.cloud.vcs.git.impl.vcs.GitlabServerVCS;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GitlabVCSTest {
//    @ClassRule
//    public static DockerComposeContainer gitlab;

    @ClassRule
    public static DockerComposeContainer gitlab = new DockerComposeContainer(
            new File("../../docker/v2/docker-compose.yml"))
            .withEnv("api.version", "1.23")
            .withExposedService("gitlab", 8083)
            .withExposedService("gitlab", 8322);

//    static{
//        try {
//            gitlab = new DockerComposeContainer(
//                    new File("../../docker/v2/docker-compose.yml"))
//                    .withEnv("api.version", "1.23")
//                    .withExposedService("gitlab", 8083)
//                    .withExposedService("gitlab", 8322);
//        }catch (NullPointerException ex)
//        {
//            //Do nothing
//        }
//
//    }

    private final GitlabServerVCS vcs;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    public GitlabVCSTest() {
        this.vcs = new GitlabServerVCS(new InitOnceAdminBean());
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
        assertThat(vcs.isEnabled(), is(true));
    }
}
