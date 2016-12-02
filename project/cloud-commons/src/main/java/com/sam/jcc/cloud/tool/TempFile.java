package com.sam.jcc.cloud.tool;

import java.io.File;

/**
 * @author Alexey Zhytnik
 * @since 29.11.2016
 */
public class TempFile extends File implements AutoCloseable {

    private static FileManager fileManager = new FileManager();

    TempFile(File file) {
        super(file.toString());
    }

    @Override
    public void close() {
        fileManager.delete(this);
    }
}
