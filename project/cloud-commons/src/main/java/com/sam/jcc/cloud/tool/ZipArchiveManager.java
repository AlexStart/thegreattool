package com.sam.jcc.cloud.tool;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
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
        try (ZipFile zip = new ZipFile(archive)) {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                final ZipEntry e = entries.nextElement();

                if (e.isDirectory()) {
                    createDirByEntry(dest, e);
                } else {
                    createFileByEntry(dest, zip, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDirByEntry(File dest, ZipEntry entry) throws IOException {
        final Path path = getPath(entry, dest);
        Files.createDirectories(path);
    }

    private void createFileByEntry(File dest, ZipFile zip, ZipEntry entry) throws IOException {
        try (final InputStream in = new BufferedInputStream(zip.getInputStream(entry))) {
            final Path path = getPath(entry, dest);
            Files.createFile(path);
            try (OutputStream out = new FileOutputStream(path.toFile())) {
                copy(in, out);
            }
        }
    }

    private Path getPath(ZipEntry entry, File dir) {
        final String name = entry.getName();
        final Path path = dir.toPath().resolve(name);
        final File parent = path.toFile().getParentFile();

        if (!parent.exists()) {
            files.createDir(parent);
        }
        return path;
    }

    public byte[] zip(File dir) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream((int) dir.length());

        try (ZipOutputStream zip = new ZipOutputStream(buffer)) {
            addDir(zip, dir, dir);
            zip.finish();
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addDir(ZipOutputStream zip, File root, File current) throws IOException {
        File[] files = current.listFiles();
        if (isNull(files) || files.length == 0) return;

        for (File file : files) {
            if (file.isDirectory()) {
                addDir(zip, root, file);
                continue;
            }
            createZipEntry(zip, root, file);
        }
    }

    private void createZipEntry(ZipOutputStream zip, File root, File file) throws IOException {
        try (FileInputStream resource = new FileInputStream(file)) {
            zip.putNextEntry(new ZipEntry(getRelativePath(root, file)));
            copy(resource, zip);
        } finally {
            zip.closeEntry();
        }
    }

    private String getRelativePath(File root, File file) {
        return root.toPath().relativize(file.toPath()).toString();
    }
}
