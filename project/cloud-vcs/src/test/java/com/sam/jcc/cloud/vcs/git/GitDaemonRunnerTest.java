package com.sam.jcc.cloud.vcs.git;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

/**
 * @author Alexey Zhytnik
 * @since 05.12.2016
 */
public class GitDaemonRunnerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void runsGitDaemon() throws IOException {
        final Process git = new GitDaemonRunner().run(temp.newFolder());
        git.destroyForcibly();
    }
}