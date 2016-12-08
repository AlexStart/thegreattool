package com.sam.jcc.cloud.utils.files;

import java.io.File;

/**
 * @author Alexey Zhytnik
 * @since 29.11.2016
 */
public class TempFile extends File implements AutoCloseable {

    private static FileManager fileManager = new FileManager();

    public TempFile(File file) {
        super(file.getAbsolutePath());
    }

    public TempFile(String path) {
        super(path);
    }

    @Override
    public void close() {
        fileManager.delete(this);
    }
}
