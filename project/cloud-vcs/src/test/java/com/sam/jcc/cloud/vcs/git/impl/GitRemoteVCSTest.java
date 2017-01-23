package com.sam.jcc.cloud.vcs.git.impl;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper;
import com.sam.jcc.cloud.vcs.utils.GitDaemon;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Alexey Zhytnik
 * @since 06-Dec-16
 */
public class GitRemoteVCSTest {

    @ClassRule
    public static TemporaryFolder temp = new TemporaryFolder();

    static GitDaemon daemon;

    GitVCS git = new GitVCS();
    VCSRepository repository = VCSRepositoryDataHelper.repository();

    @BeforeClass
    public static void startUpDaemon() throws Exception {
        daemon = new GitDaemon();
        daemon.startUp(temp.newFolder());
    }

    @AfterClass
    public static void shutDownDaemon() {
        daemon.shutDown();
    }

    @Before
    public final void setUp() throws Exception {
        final GitRemoteStorage storage = new GitRemoteStorage();
        storage.setBaseRepository(daemon.getStorage());

        git.setStorage(storage);
    }

    @Test
    public void createsAndDeletes() throws IOException {
        git.create(repository);
        git.delete(repository);
    }

    @Test
    public void reads() throws IOException {
        git.create(repository);
        try {
            final Entry<String, byte[]> data = writeSomeDataAndCommit();

            final File dest = temp.newFolder();
            repository.setSources(dest);

            git.read(repository);
            assertThat(dest.listFiles()).isNotNull().isNotEmpty();

            final File copy = new File(dest, data.getKey());

            assertThat(copy)
                    .exists()
                    .isFile()
                    .hasBinaryContent(data.getValue());
        } finally {
            invalidateAndDelete(repository);
        }
    }

    @Test
    public void worksStable() throws IOException {
        git.create(repository);

        try {
            writeSomeDataAndCommit();
            writeSomeDataAndCommit();

            repository.setSources(temp.newFolder());
            git.read(repository);
        } finally {
            invalidateAndDelete(repository);
        }
    }

    @Test
    public void checksExistence() throws IOException {
        assertThat(git.isExist(repository)).isFalse();

        git.create(repository);

        assertThat(git.isExist(repository)).isTrue();

        invalidateAndDelete(repository);

        assertThat(git.isExist(repository)).isFalse();
    }

    @Test
    public void commits() throws IOException {
        git.create(repository);
        try {
            writeSomeDataAndCommit();
        } finally {
            invalidateAndDelete(repository);
        }
    }

    @Test
    public void integrationTest() throws IOException {
        assertThat(git.isExist(repository)).isFalse();
        git.create(repository);
        assertThat(git.isExist(repository)).isTrue();

        repository.setSources(temp.newFolder());
        try {
            writeSomeDataAndCommit();

            repository.setSources(temp.newFolder());
            git.read(repository);

            assertThat(repository.getSources().listFiles())
                    .isNotNull()
                    .isNotEmpty();
        } finally {
            invalidateAndDelete(repository);
        }
    }

    void invalidateAndDelete(VCSRepository repository) throws IOException {
        daemon.disableExport(repository);
        git.delete(repository);
    }

    Entry<String, byte[]> writeSomeDataAndCommit() throws IOException {
        final Random random = new Random();
        final File src = temp.newFolder();
        final File file = new File(src, random.nextInt() + "-file.txt");

        final byte[] content = new byte[10_000];
        random.nextBytes(content);
        new FileManager().write(content, file);

        repository.setSources(src);

        git.commit(repository);

        return entry(file.getName(), content);
    }
}
