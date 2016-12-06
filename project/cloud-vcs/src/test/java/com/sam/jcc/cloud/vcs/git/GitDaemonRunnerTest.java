package com.sam.jcc.cloud.vcs.git;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 05.12.2016
 */
public class GitDaemonRunnerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Before
    public void needsInstalledGit() {
        if (IS_OS_WINDOWS) {
            assertThat(System.getenv("path")).containsIgnoringCase("git");
        }
    }

    @Test
    public void runsGitDaemon() throws IOException {
        final Process git = new GitDaemonRunner().run(temp.newFolder());

        new ProcessKiller().kill(git);
    }
}