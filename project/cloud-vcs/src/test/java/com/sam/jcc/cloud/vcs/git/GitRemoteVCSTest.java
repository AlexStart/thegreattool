package com.sam.jcc.cloud.vcs.git;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import com.sam.jcc.cloud.vcs.utils.GitDaemonRunner;
import com.sam.jcc.cloud.vcs.utils.ProcessKiller;

/**
 * @author Alexey Zhytnik
 * @since 06-Dec-16
 */
@Ignore // TODO fix Issue #9 
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
