package com.sam.jcc.cloud.utils.files;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import com.sam.jcc.cloud.exception.InternalCloudException;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static one.util.streamex.EntryStream.zip;

/**
 * @author Alexey Zhytnik
 * @since 02-Dec-16
 */
@Slf4j
public final class DirectoryComparator {

    private FileManager files = new FileManager();

    public boolean areEquals(File a, File b) {
        final Optional<Entry<File, File>> diff =
                zip(
                        allFiles(a),
                        allFiles(b)
                )
                        .filter(this::notEquals)
                        .findAny();
        return !diff.isPresent();
    }

    private boolean notEquals(Entry<File, File> pair) {
        try (BufferedReader first = getReader(pair.getKey());
             BufferedReader second = getReader(pair.getValue())) {

            String a = "", b = "";
            boolean equals = true;

            while (equals && ((a = first.readLine()) != null) && ((b = second.readLine()) != null)) {
                equals = a.equals(b);
            }
            if (equals && a == null) b = second.readLine();

            final boolean hasDiff = !equals || nonNull(a) || nonNull(b);

            log(pair, hasDiff);
            return hasDiff;
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }

    private void log(Entry<File, File> pair, boolean hasDiff) {
        final File a = pair.getKey();
        final File b = pair.getValue();

        if (hasDiff) {
            log.info("Files {}, {} are different!", a, b);
        } else {
            log.debug("Files {}, {} are equals", a, b);
        }
    }

    private BufferedReader getReader(File file) throws FileNotFoundException {
        final FileReader reader = new FileReader(file);
        return new BufferedReader(reader);
    }

    private List<File> allFiles(File dir) {
        return files
                .getAllDirectoryFiles(dir)
                .stream()
                .sorted(
                        Comparator
                                .comparing(files::getNesting)
                                .thenComparing(File::getName)
                )
                .collect(toList());
    }
}
