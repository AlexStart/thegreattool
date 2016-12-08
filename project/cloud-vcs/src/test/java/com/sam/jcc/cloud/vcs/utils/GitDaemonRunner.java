package com.sam.jcc.cloud.vcs.utils;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 05.12.2016
 */
public class GitDaemonRunner {

    /**
     * Runs Git-daemon: 2 Git + 1 Git-daemon processes.
     * If push command doesn't work, maybe it's because of the Git bug.
     *
     * @see <a href="http://stackoverflow.com/q/5520329">Git daemon bug</a>
     */
    public Process run(File dir) {
        final ProcessBuilder builder = new ProcessBuilder()
                .command(getDaemonRunCommands(dir));
        try {
            final Process git = builder.start();
            failOnDeadState(git);
            startUpWait();
            return git;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sometimes disappearing parent-child relationship.
     * Needs for launch all Git processes.
     */
    private void startUpWait() throws InterruptedException {
        Thread.sleep(600);
    }

    private List<String> getDaemonRunCommands(File dir) {
        return ImmutableList.of(
                "git",
                "daemon",
                "--reuseaddr",
                getWalkingDir(dir),
                "--export-all",
                "--enable=receive-pack"
        );
    }

    private String getWalkingDir(File dir) {
        final String path = dir.getAbsolutePath();
        return format("--base-path=\"{0}\"", path);
    }

    private void failOnDeadState(Process p) {
        if (!p.isAlive()) {
            throw new RuntimeException("Git-daemon was not started!");
        }
    }
}