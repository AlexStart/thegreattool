package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.vcs.utils.GitDaemonRunner;
import com.sam.jcc.cloud.vcs.utils.ProcessKiller;
import org.junit.After;
import org.junit.Before;

import java.io.File;

/**
 * @author Alexey Zhytnik
 * @since 06-Dec-16
 */
public class GitRemoteVCSTest extends AbstractGitVCSTest {

    File dir;
    Process daemon;

    @Override
    protected void installStorage(GitVCS git, File dir) {
        this.dir = dir;

        final GitRemoteStorage storage = new GitRemoteStorage();
        storage.setBaseRepository(dir);

        git.setStorage(storage);
    }

    @Before
    public void startUpGitDaemon() {
        daemon = new GitDaemonRunner().run(dir);
    }

    @After
    public void shutDownGitDaemon() {
        new ProcessKiller().kill(daemon);
    }
}
