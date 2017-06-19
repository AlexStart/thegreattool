package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import lombok.Setter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
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
    @Ignore
    public void checksExistenceAfterCreateDelete() {
        assertThat(vcs.isExist(repository)).isFalse();

        vcs.create(repository);

        assertThat(vcs.isExist(repository)).isTrue();

        vcs.delete(repository);

        assertThat(vcs.isExist(repository)).isFalse();
    }

    @Test
    @Ignore
    public void worksStable() throws IOException {
        vcs.create(repository);

        writeSomeBinaryDataAndCommit();
        writeSomeBinaryDataAndCommit();

        repository.setSources(temp.newFolder());
        vcs.read(repository);
    }

    @Test(expected = VCSRepositoryNotFoundException.class)
    @Ignore
    public void failsOnDeleteNotExistence() {
        vcs.delete(repository);
    }

    protected Entry<String, byte[]> writeSomeBinaryDataAndCommit() throws IOException {
        final File src = temp.newFolder();
        final File file = new File(src, new Random().nextInt() + "-file.txt");
        byte[] content = writeRandomBinaryToFile(file);
        repository.setSources(src);

        vcs.commit(repository);

        return entry(file.getName(), content);
    }

    protected byte[] writeRandomBinaryToFile(File file) {
        final Random random = new Random();
        final byte[] content = new byte[10_000];
        random.nextBytes(content);
        new FileManager().write(content, file);

        return content;
    }

    protected String writeRandomStringToFile(File file) throws IOException {
        final String content = UUID.randomUUID().toString();
        writeStringToFile(file, content, UTF_8);

        return content;
    }
}