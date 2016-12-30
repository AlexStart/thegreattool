package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper;
import org.junit.Before;
import org.junit.Rule;
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
 * @since 28.11.2016
 */
public class GitFileVCSTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private final GitVCS git = new GitVCS();
    private final VCSRepository repository = VCSRepositoryDataHelper.repository();

    @Before
    public void setUp() throws IOException {
        final GitFileStorage storage = new GitFileStorage();
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