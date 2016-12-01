package com.sam.jcc.cloud.tool;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

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

    public TempFile createTempDir() {
        return new TempFile(Files.createTempDir());
    }
}
