package com.sam.jcc.cloud.project;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.Objects.isNull;
import static org.apache.commons.io.IOUtils.copy;

/**
 * @author Alexey Zhytnik
 * @since 16.11.2016
 */
class ZipUtil {

    private ZipUtil() {
    }

    public static byte[] archivateDir(File dir) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream((int) dir.length());

        try (ZipOutputStream zip = new ZipOutputStream(buffer)) {
            addDir(zip, dir);
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addDir(ZipOutputStream zip, File dir) throws IOException {
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

    private static void createZipEntry(ZipOutputStream zip, File file) throws IOException {
        try (FileInputStream resource = new FileInputStream(file)) {
            zip.putNextEntry(new ZipEntry(file.getAbsolutePath()));
            copy(resource, zip);
        } finally {
            zip.closeEntry();
        }
    }
}
