package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.git.impl.storage.GitRemoteStorage;
import com.sam.jcc.cloud.vcs.utils.GitDaemon;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * @author Alexey Zhytnik
 * @since 06-Dec-16
 */
public class GitRemoteVCSTest extends AbstractVCSTest {

    @ClassRule
    public static TemporaryFolder temp = new TemporaryFolder();

    static GitDaemon daemon;

    public GitRemoteVCSTest() {
        super(new GitVCS());
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
        final GitRemoteStorage storage = new GitRemoteStorage();
        storage.setBaseRepository(daemon.getStorage());
        storage.setPort(daemon.getCurrentPort());
        vcs.setStorage(storage);
        setTemp(temp);
    }

    @After
    public void tearDown() throws IOException {
        if (vcs.isExist(repository)) {
            daemon.disableExport(repository);
            vcs.delete(repository);
        }
    }

    /**
     * GitDaemon bug - see daemon.disableExport comments
     */
    @Override
    public void vscDelete() {
        try {
            daemon.disableExport(repository);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        vcs.delete(repository);
    }
}
