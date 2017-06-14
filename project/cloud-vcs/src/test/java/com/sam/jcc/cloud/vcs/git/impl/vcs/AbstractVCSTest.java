package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper;
import lombok.Setter;
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
public abstract class AbstractVCSTest {

    @Setter
    private TemporaryFolder temp;

    protected final VCSRepository repository = VCSRepositoryDataHelper.repository();
    protected final VCS vcs;

    protected AbstractVCSTest(VCS vcs) {
        this.vcs = vcs;
    }

    @Test
    public void createsAndDeletes() throws IOException {
        vcs.create(repository);
        vcs.delete(repository);
    }

    @Test
    public void reads() throws IOException {
        vcs.create(repository);
        final Entry<String, byte[]> data = writeSomeDataAndCommit();

        final File dest = temp.newFolder();
        repository.setSources(dest);

        vcs.read(repository);
        assertThat(dest.listFiles()).isNotNull().isNotEmpty();

        final File copy = new File(dest, data.getKey());

        assertThat(copy)
                .exists()
                .isFile()
                .hasBinaryContent(data.getValue());
    }

    @Test
    public void worksStable() throws IOException {
        vcs.create(repository);

        writeSomeDataAndCommit();
        writeSomeDataAndCommit();

        repository.setSources(temp.newFolder());
        vcs.read(repository);
    }

    @Test
    public void checksExistence() {
        assertThat(vcs.isExist(repository)).isFalse();

        vcs.create(repository);

        assertThat(vcs.isExist(repository)).isTrue();

        vcs.delete(repository);

        assertThat(vcs.isExist(repository)).isFalse();
    }

    @Test
    public void commits() throws IOException {
        vcs.create(repository);
        writeSomeDataAndCommit();
    }

    @Test
    public void integrationTest() throws IOException {
        assertThat(vcs.isExist(repository)).isFalse();
        vcs.create(repository);
        assertThat(vcs.isExist(repository)).isTrue();

        repository.setSources(temp.newFolder());
        writeSomeDataAndCommit();

        repository.setSources(temp.newFolder());
        vcs.read(repository);

        assertThat(repository.getSources().listFiles())
                .isNotNull()
                .isNotEmpty();

        //TODO doesn't work for daemon
        vscDelete();
        assertThat(vcs.isExist(repository)).isFalse();
    }

    Entry<String, byte[]> writeSomeDataAndCommit() throws IOException {
        final Random random = new Random();
        final File src = temp.newFolder();
        final File file = new File(src, random.nextInt() + "-file.txt");

        final byte[] content = new byte[10_000];
        random.nextBytes(content);
        new FileManager().write(content, file);

        repository.setSources(src);

        vcs.commit(repository);

        return entry(file.getName(), content);
    }

    public void vscDelete() {
        vcs.delete(repository);
    }
}