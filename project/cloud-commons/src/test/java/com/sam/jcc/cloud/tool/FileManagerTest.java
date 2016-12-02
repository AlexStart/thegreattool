package com.sam.jcc.cloud.tool;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.lang.System.getProperty;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 01.12.2016
 */
public class FileManagerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    FileManager fileManager = new FileManager();

    @Test
    public void deletesDirectoryFiles() throws IOException {
        final File dir = temp.newFolder();
        createInnerFile(dir);

        fileManager.delete(dir);
        assertThat(dir).doesNotExist();
    }

    @Test
    public void deletesFiles() throws IOException {
        final File file = temp.newFile();

        fileManager.delete(file);
        assertThat(file).doesNotExist();
    }

    @Test
    public void cleansDirectory() throws IOException {
        final File dir = temp.newFolder();
        createInnerFile(dir);

        fileManager.cleanDir(dir);

        assertThat(dir).exists();
        assertThat(dir.listFiles()).isNullOrEmpty();
    }

    @Test
    public void copiesDirectory() throws IOException {
        throw new NotImplementedException("is it important?");
    }

    @Test
    public void createsHiddenDirectory() throws IOException {
        final File file = temp.newFile("tmp");

        fileManager.createHiddenDir(file);

        assertThat(file)
                .exists()
                .isDirectory()
                .canRead()
                .canWrite();
        assertThat(file.isHidden()).isTrue();
    }

    @Test
    public void createsDirectory() throws IOException {
        final File file = temp.newFile("tmp");

        fileManager.createDir(file);

        assertThat(file)
                .exists()
                .isDirectory()
                .canRead()
                .canWrite();
    }

    @Test
    public void createsTempDir() throws IOException {
        final TempFile temp = fileManager.createTempDir();

        assertThat(temp)
                .hasParent(getProperty("java.io.tmpdir"))
                .exists()
                .isDirectory()
                .canRead()
                .canWrite();
    }

    @Test
    public void createsTempFile() throws IOException {
        final TempFile temp = fileManager.createTempFile("file", "txt");

        assertThat(temp)
                .exists()
                .canRead()
                .canWrite();
    }

    @Test
    public void writesToFile() throws IOException {
        final File file = temp.newFile();
        final byte[] content = randomContent();

        fileManager.write(content, file);

        assertThat(file).hasBinaryContent(content);
    }

    File createInnerFile(File dir) throws IOException {
        File inner = new File(dir, randomUUID().toString());
        if (!inner.createNewFile()) {
            throw new RuntimeException("Can't create file " + inner);
        }
        return inner;
    }

    byte[] randomContent() {
        final byte[] content = new byte[10_000];
        new Random().nextBytes(content);
        return content;
    }
}