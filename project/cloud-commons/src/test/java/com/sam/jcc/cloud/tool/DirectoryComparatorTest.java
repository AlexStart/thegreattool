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
public class DirectoryComparatorTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    FileManager files = new FileManager();
    DirectoryComparator comparator = new DirectoryComparator();

    @Test
    public void comparesOnlyByFileContent() throws IOException {
        assertThat(comparator.areEquals(buildFileStructure(), buildFileStructure()));
    }

    File buildFileStructure() throws IOException {
        final Random random = new Random(0);
        final File root = temp.newFolder();

        files.write(randomContent(random), new File(root, "1"));

        files.createDir(new File(root, "a"));
        files.write(randomContent(random), new File(root, "a/1"));

        files.createDir(new File(root, "a/b"));
        files.write(randomContent(random), new File(root, "a/b/1"));

        return root;
    }

    byte[] randomContent(Random random) {
        final byte[] content = new byte[10_000];
        random.nextBytes(content);
        return content;
    }
}