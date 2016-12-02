package com.sam.jcc.cloud.tool;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Random;

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

        files.write(randomContent(), new File(root, "1"));
        files.write(randomContent(), new File(root, "2"));
        files.write(randomContent(), new File(root, "3"));

        files.createDir(new File(root, "a"));
        files.write(randomContent(), new File(root, "a/1"));
        files.write(randomContent(), new File(root, "a/2"));
        files.write(randomContent(), new File(root, "a/3"));

        files.createDir(new File(root, "a/b"));
        files.write(randomContent(), new File(root, "a/b/1"));

        return root;
    }

    byte[] randomContent() {
        final byte[] content = new byte[10_000];
        new Random().nextBytes(content);
        return content;
    }
}