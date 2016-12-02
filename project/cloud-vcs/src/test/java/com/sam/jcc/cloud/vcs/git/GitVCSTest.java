package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.tool.FileManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper.repository;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public class GitVCSTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    GitVCS git = new GitVCS();
    VCSRepository repository = repository();

    @Before
    public void setUp() throws IOException {
        final GitLocalStorage storage = new GitLocalStorage();
        storage.setBaseRepository(temp.newFolder());

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
        writeSomeDataAndCommit();

        final File dest = temp.newFolder();
        repository.setSources(dest);

        git.read(repository);
        assertThat(dest.listFiles()).isNotNull().isNotEmpty();
    }

    @Test
    public void worksStable() throws IOException {
        git.create(repository);

        writeSomeDataAndCommit();
        writeSomeDataAndCommit();

        repository.setSources(temp.newFolder());
        git.read(repository);
    }

    @Test
    public void checksExistence() {
        assertThat(git.isExist(repository)).isFalse();

        git.create(repository);

        assertThat(git.isExist(repository)).isTrue();
    }

    @Test
    public void commits() throws IOException {
        git.create(repository);
        writeSomeDataAndCommit();
    }

    @Test
    public void integrationTest() throws IOException {
        assertThat(git.isExist(repository)).isFalse();
        git.create(repository);
        assertThat(git.isExist(repository)).isTrue();

        repository.setSources(temp.newFolder());
        writeSomeDataAndCommit();

        repository.setSources(temp.newFolder());
        git.read(repository);

        assertThat(repository.getSources().listFiles())
                .isNotNull()
                .isNotEmpty();

        git.delete(repository);
        assertThat(git.isExist(repository)).isFalse();
    }

    void writeSomeDataAndCommit() throws IOException {
        final Random random = new Random();
        final File src = temp.newFolder();
        final File file = new File(src, random.nextInt() + "-file.txt");

        final byte[] content = new byte[10_000];
        random.nextBytes(content);
        new FileManager().write(content, file);

        repository.setSources(src);

        git.commit(repository);
    }
}