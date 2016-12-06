package com.sam.jcc.cloud.utils.files;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.apache.commons.io.IOUtils.toByteArray;

/**
 * @author Alexey Zhytnik
 * @since 16.11.2016
 */
@Component
public class ZipArchiveManager {

    private FileManager files = new FileManager();

    public boolean isZip(File file) {
        return file.getName().endsWith(".zip");
    }

    public ZipFile getZipFile(File file) {
        try {
            return new ZipFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readEntry(ZipFile zip, ZipEntry entry) {
        try {
            final InputStream stream = zip.getInputStream(entry);
            return IOUtils.toString(stream, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unzip(File archive, File dest) {
        try {
            new net.lingala.zip4j.core.ZipFile(archive).extractAll(dest.getAbsolutePath());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Applies zip operation for all inner files & directories of a directory
     * and converts it to the byte array.
     */
    public byte[] zip(File dir) {
        try {
            try (TempFile temp = createTempZip()) {
                new net.lingala.zip4j.core.ZipFile(temp).addFolder(dir, getZipParameters());
                return toByteArray(temp.toURI());
            }
        } catch (ZipException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TempFile createTempZip() {
        final TempFile file = files.createTempFile("temp", ".zip");
        files.delete(file);
        return file;
    }

    private ZipParameters getZipParameters() {
        final ZipParameters params = new ZipParameters();
        params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        params.setIncludeRootFolder(false);
        return params;
    }
}
