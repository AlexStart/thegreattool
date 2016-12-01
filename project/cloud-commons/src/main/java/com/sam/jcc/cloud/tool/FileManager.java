package com.sam.jcc.cloud.tool;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Optional;

import static java.nio.file.Files.setAttribute;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static org.apache.commons.io.FileUtils.copyDirectory;

/**
 * @author Alexey Zhytnik
 * @since 21.11.2016
 */
@Component
public class FileManager {

    public void deleteDir(File dir) {
        cleanDir(dir);

        if (!dir.delete()) {
            throw new RuntimeException("Can't deleteDir " + dir);
        }
    }

    public void cleanDir(File dir) {
        try {
            FileUtils.cleanDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyDir(File src, File dest) {
        try {
            copyDirectory(src, dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createHiddenFolder(File file) {
        if (!file.mkdir()) {
            throw new RuntimeException("Can't create folder " + file);
        }
        try {
            setAttribute(file.toPath(), "dos:hidden", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<File> search(File root, FilenameFilter filter) {
        final File[] files = root.listFiles(filter);

        if (nonNull(files) && files.length > 0) {
            return Optional.of(files[0]);
        }
        return empty();
    }

    public TempFile createTempDir() {
        return new TempFile(Files.createTempDir());
    }
}
