package com.sam.jcc.cloud.vcs.git;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import com.sam.jcc.cloud.vcs.utils.GitDaemonRunner;

/**
 * @author Alexey Zhytnik
 * @since 06-Dec-16
 */
public class GitRemoteVCSTest extends AbstractGitVCSTest {

    File dir;
    GitDaemonRunner daemon;

    @Override
    protected void installStorage(GitVCS git, File dir) {
        this.dir = dir;

        final GitRemoteStorage storage = new GitRemoteStorage();
        storage.setBaseRepository(dir);

        git.setStorage(storage);
    }

    @Before
    public void startUpGitDaemon() {
    	// TODO use properties
    	daemon = new GitDaemonRunner("localhost", 19418);
		daemon.start(dir);
    }

    @After
    public void shutDownGitDaemon() {
    	daemon.stop();
    }
}
