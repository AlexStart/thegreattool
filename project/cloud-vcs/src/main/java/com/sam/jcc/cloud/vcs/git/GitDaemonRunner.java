package com.sam.jcc.cloud.vcs.git;

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

    public Process run(File dir) {
        final ProcessBuilder builder = new ProcessBuilder()
                .command(getDaemonRunCommands(dir));

        try {
            return builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getDaemonRunCommands(File dir) {
        return ImmutableList.of(
                "git",
                "daemon",
                "--reuseaddr",
                getWalkingDir(dir),
                "--export-all",
                "--verbose",
                "--enable=receive-pack"
        );
    }

    private String getWalkingDir(File dir) {
        final String path = dir.getAbsolutePath();
        return format("--base-path=\"{0}\"", path);
    }
}