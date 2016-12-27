package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.event.DefaultLoggingEventManager;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Random;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public abstract class AbstractGitVCSTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private GitVCS git = new GitVCS();
    private VCSRepository repository = VCSRepositoryDataHelper.repository();

    @Before
    public final void setUp() throws IOException {
        final File dir = temp.newFolder();

        git.setEventManagers(singletonList(new DefaultLoggingEventManager<>()));

        installStorage(git, dir);
    }

    protected abstract void installStorage(GitVCS git, File dir);

    @Test
    @Ignore
    public void createsAndDeletes() throws IOException {
        git.create(repository);
        git.delete(repository);
    }

    @Test
    @Ignore
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
    @Ignore
    public void worksStable() throws IOException {
        git.create(repository);

        writeSomeDataAndCommit();
        writeSomeDataAndCommit();

        repository.setSources(temp.newFolder());
        git.read(repository);
    }

    @Test
    @Ignore
    public void checksExistence() {
        assertThat(git.isExist(repository)).isFalse();

        git.create(repository);

        assertThat(git.isExist(repository)).isTrue();
    }

    @Test(timeout = 10_000L)
    public void commits() throws IOException {
        git.create(repository);
        writeSomeDataAndCommit();
    }

    @Test
    @Ignore
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