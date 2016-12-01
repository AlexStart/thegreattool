package com.sam.jcc.cloud.tool;

import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static java.util.Objects.isNull;
import static org.apache.commons.io.IOUtils.copy;

/**
 * @author Alexey Zhytnik
 * @since 16.11.2016
 */
@Component
public class ZipArchiveManager {

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
            final net.lingala.zip4j.core.ZipFile zip = new net.lingala.zip4j.core.ZipFile(archive);
            zip.extractAll(dest.toString());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] zip(File dir) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream((int) dir.length());

        try (ZipOutputStream zip = new ZipOutputStream(buffer)) {
            addDir(zip, dir);
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addDir(ZipOutputStream zip, File dir) throws IOException {
        File[] files = dir.listFiles();
        if (isNull(files)) return;

        for (File file : files) {
            if (file.isDirectory()) {
                addDir(zip, file);
                continue;
            }
            createZipEntry(zip, file);
        }
    }

    private void createZipEntry(ZipOutputStream zip, File file) throws IOException {
        try (FileInputStream resource = new FileInputStream(file)) {
            zip.putNextEntry(new ZipEntry(file.getAbsolutePath()));
            copy(resource, zip);
        } finally {
            zip.closeEntry();
        }
    }
}
