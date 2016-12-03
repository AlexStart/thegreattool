package com.sam.jcc.cloud.tool;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static com.google.common.hash.Hashing.md5;
import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 02-Dec-16
 */
public final class DirectoryComparator {

    private FileManager files = new FileManager();

    public boolean areEquals(File a, File b) {
        return hash(allFiles(a)) == hash(allFiles((b)));
    }

    private List<File> allFiles(File dir) {
        return files
                .getDirectoryFiles(dir)
                .stream()
                .sorted(
                        Comparator
                                .comparing(files::getNesting)
                                .thenComparing(File::getName)
                )
                .collect(toList());
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
