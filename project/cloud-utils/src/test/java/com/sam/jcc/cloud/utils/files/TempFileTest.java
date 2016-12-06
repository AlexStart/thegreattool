package com.sam.jcc.cloud.utils.files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 29.11.2016
 */
public class TempFileTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void removesFileAutomatically() throws IOException {
        final File file = temp.newFile();

        try (TempFile tmp = new TempFile(file)) {
            assertThat(tmp.exists()).isTrue();
        }
        assertThat(file.exists()).isFalse();
    }

    @Test
    public void removesFolderAutomatically() throws IOException {
        final File file = temp.newFolder();

        try (TempFile tmp = new TempFile(file)) {
            assertThat(tmp.exists()).isTrue();
        }
        assertThat(file.exists()).isFalse();
    }
}