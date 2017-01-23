package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.function.Consumer;

/**
 * @author Alexey Zhytnik
 * @since 18.01.2017
 */
@Component
@Experimental("for simple work with zip data")
class UnzipSandbox {

    @Autowired
    private FileManager files;

    @Autowired
    private ZipArchiveManager zipManager;

    public byte[] apply(byte[] zip, Consumer<File> consumer) {
        try (TempFile tmp = files.createTempFile(); TempFile sources = files.createTempDir()) {
            files.write(zip, tmp);
            zipManager.unzip(tmp, sources);

            consumer.accept(sources);

            return zipManager.zip(sources);
        }
    }
}
