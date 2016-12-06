package com.sam.jcc.cloud.utils.files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static com.sam.jcc.cloud.utils.files.TestFileUtils.createFolder;
import static com.sam.jcc.cloud.utils.files.TestFileUtils.fileWithRand;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 02.12.2016
 */
public class DirectoryComparatorTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    DirectoryComparator comparator = new DirectoryComparator();

    @Test
    public void comparesOnlyByFileContent() throws IOException {
        assertThat(comparator.areEquals(buildFileStructure(), buildFileStructure())).isTrue();
    }

    File buildFileStructure() throws IOException {
        final Random random = new Random(0);
        final File root = temp.newFolder();

        fileWithRand(new File(root, "1"), random);

        createFolder(new File(root, "a"));
        fileWithRand(new File(root, "a/1"), random);

        createFolder(new File(root, "a/b"));
        fileWithRand(new File(root, "a/b/1"), random);
        return root;
    }
}