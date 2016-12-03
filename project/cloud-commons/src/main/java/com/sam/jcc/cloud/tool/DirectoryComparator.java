package com.sam.jcc.cloud.tool;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static com.google.common.hash.Hashing.md5;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.filefilter.DirectoryFileFilter.DIRECTORY;
import static org.apache.commons.io.filefilter.FileFileFilter.FILE;

/**
 * @author Alexey Zhytnik
 * @since 02-Dec-16
 */
public final class DirectoryComparator {

    public boolean areEquals(File a, File b) {
        return hash(allFiles(a)) == hash(allFiles((b)));
    }

    private Collection<File> allFiles(File dir) {
        return listFiles(dir, FILE, DIRECTORY);
    }

    private long hash(Collection<File> files) {
        return files.stream()
                .mapToLong(this::hash)
                .reduce(17, (h, value) -> 31 * h + value);
    }

    private long hash(File file) {
        try {
            return Files.hash(file, md5()).asLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
