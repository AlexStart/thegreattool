package com.sam.jcc.cloud.vcs.utils;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Alexey Zhytnik
 * @since 05.12.2016
 */
public class GitDaemonRunnerTest {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Before
	public void needsInstalledGit() {
		assertThat(System.getenv("path")).containsIgnoringCase("git");
	}

	@Test(timeout = 10_000L)
	public void runsGitDaemon() throws IOException {
		// TODO use properties instead
		GitDaemonRunner gitDaemonRunner = new GitDaemonRunner("localhost", 19418);
		gitDaemonRunner.start(temp.newFolder());
		//
		System.out.println(gitDaemonRunner.getHost());
		System.out.println(gitDaemonRunner.getPort());
		//
		gitDaemonRunner.stop();
	}
}