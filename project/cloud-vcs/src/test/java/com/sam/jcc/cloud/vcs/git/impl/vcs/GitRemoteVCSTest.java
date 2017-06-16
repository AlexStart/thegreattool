package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.exception.VCSRepositoryAlreadyExistsException;
import com.sam.jcc.cloud.vcs.utils.GitDaemon;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 06-Dec-16
 */
public class GitRemoteVCSTest extends AbstractVCSTest {

    @ClassRule
    public static TemporaryFolder temp = new TemporaryFolder();

    static GitDaemon daemon;

    private final GitRemoteVCS vcs;

    public GitRemoteVCSTest() {
        super(new GitRemoteVCS());
        this.vcs = (GitRemoteVCS) super.vcs;
    }

    @BeforeClass
    public static void startUpDaemon() throws Exception {
        daemon = new GitDaemon();
        daemon.startUp(temp.newFolder());
    }

    @AfterClass
    public static void shutDownDaemon() {
        daemon.shutDown();
    }

    @Before
    public final void setUp() throws Exception {
        vcs.setBaseRepository(daemon.getStorage());
        vcs.setPort(daemon.getCurrentPort());
        setTemp(temp);
    }

    @After
    public void tearDown() throws IOException {
        if (vcs.isExist(repository)) {
            daemon.disableExport(repository);
            vcs.delete(repository);
        }
    }

    @Test(expected = VCSRepositoryAlreadyExistsException.class)
    public void failsOnCreationExistence() {
        vcs.create(repository);
        vcs.create(repository);
    }

    @Test
    public void commitRead() throws IOException {
        vcs.create(repository);
        final File dest = temp.newFolder();
        repository.setSources(dest);

        Map.Entry<String, byte[]> data = writeSomeBinaryDataAndCommit();
        repository.setSources(dest);
        vcs.read(repository);

        assertThat(dest.listFiles()).isNotNull().isNotEmpty();
        final File copy = new File(dest, data.getKey());
        assertThat(copy).exists().isFile().hasBinaryContent(data.getValue());
    }
}
