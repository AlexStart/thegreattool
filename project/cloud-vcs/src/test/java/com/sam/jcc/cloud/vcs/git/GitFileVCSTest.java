package com.sam.jcc.cloud.vcs.git;

import java.io.File;

/**
 * @author Alexey Zhytnik
 * @since 06-Dec-16
 */
public class GitFileVCSTest extends AbstractGitVCSTest {

    @Override
    protected void installStorage(GitVCS git, File dir) {
        final GitFileStorage storage = new GitFileStorage();
        storage.setBaseRepository(dir);

        git.setStorage(storage);
    }
}
