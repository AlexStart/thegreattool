package com.sam.jcc.cloud.vcs.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Alexey Zhytnik
 * @since 05.12.2016
 */
public class GitDaemonTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    GitDaemon daemon = new GitDaemon();

    @Test
    public void works() throws Exception {
        daemon.startUp(temp.newFolder());
        daemon.shutDown();
    }
}