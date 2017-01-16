package com.sam.jcc.cloud.utils.files;

import com.sam.jcc.cloud.exception.InternalCloudException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static com.sam.jcc.cloud.utils.SystemUtils.isWindowsOS;
import static com.sam.jcc.cloud.utils.files.TestFileUtils.createInnerFile;
import static com.sam.jcc.cloud.utils.files.TestFileUtils.randomContent;
import static java.lang.System.getProperty;
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
    public void createsHiddenDirectory() throws IOException {
        final File file = temp.newFile("tmp");

        fileManager.createHiddenDir(file);

        assertThat(file)
                .exists()
                .isDirectory()
                .canRead()
                .canWrite();

        if (isWindowsOS()) assertThat(file.isHidden()).isTrue();
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
        final TempFile temp = fileManager.createTempFile("file", ".txt");

        assertThat(temp)
                .exists()
                .canRead()
                .canWrite()
                .hasExtension("txt");

        assertThat(temp.getName()).startsWith("file");
    }

    @Test
    public void getDirectoryFiles() throws IOException {
        final File dir = temp.newFolder();

        final File root = new File(dir, "root");
        root.mkdir();
        final File file = new File(dir, "file");
        file.createNewFile();
        final File child = new File(root, "child");
        child.createNewFile();

        assertThat(fileManager.getDirectoryFiles(dir)).containsOnly(root, file);
    }

    @Test
    public void writesToFile() throws IOException {
        final File file = temp.newFile();
        final byte[] content = randomContent();

        fileManager.write(content, file);

        assertThat(file).hasBinaryContent(content);
    }

    @Test(expected = InternalCloudException.class)
    public void failsOnUnknownResource() {
        FileManager.getResource(getClass(), "unknown resource");
    }

    @Test
    public void loadsResource() {
        final File resource = FileManager.getResource(getClass(), "/gradle-project.zip");
        assertThat(resource).isNotNull().exists();
    }
}