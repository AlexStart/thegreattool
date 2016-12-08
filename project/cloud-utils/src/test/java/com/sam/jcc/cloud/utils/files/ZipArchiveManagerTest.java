package com.sam.jcc.cloud.utils.files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static com.sam.jcc.cloud.utils.files.TestFileUtils.createFolder;
import static com.sam.jcc.cloud.utils.files.TestFileUtils.fileWithRand;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 02.12.2016
 */
public class ZipArchiveManagerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    FileManager files = new FileManager();

    ZipArchiveManager zipManager = new ZipArchiveManager();

    @Test
    public void worksWithArchives() throws IOException {
        final File src = buildFileStructure();
        final byte[] content = zipManager.zip(src);

        assertThat(content).isNotNull().isNotEmpty();

        final File zip = temp.newFile();
        files.write(content, zip);

        final File unzip = temp.newFolder();
        zipManager.unzip(zip, unzip);

        assertThat(new DirectoryComparator().areEquals(src, unzip)).isTrue();
    }

    File buildFileStructure() throws IOException {
        final File root = temp.newFolder();

        fileWithRand(new File(root, "1"));
        fileWithRand(new File(root, "2"));
        fileWithRand(new File(root, "3"));

        createFolder(new File(root, "a"));
        fileWithRand(new File(root, "a/1"));
        fileWithRand(new File(root, "a/2"));
        fileWithRand(new File(root, "a/3"));

        createFolder(new File(root, "a/b"));
        fileWithRand(new File(root, "a/b/1"));

        createFolder(new File(root, "a/b/c"));
        createFolder(new File(root, "a/b/c/d"));
        fileWithRand(new File(root, "a/b/c/d/1"));

        return root;
    }
}