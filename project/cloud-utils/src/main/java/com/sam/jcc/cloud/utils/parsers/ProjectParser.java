package com.sam.jcc.cloud.utils.parsers;

import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.Comparator.comparingInt;

/**
 * @author Alexey Zhytnik
 * @since 28-Nov-16
 */
@Slf4j
@Setter
public class ProjectParser implements IParser<File> {

    public static final String MAVEN_CONFIGURATION = "pom.xml";
    public static final String GRADLE_CONFIGURATION = "build.gradle";

    private IParser<String> mavenParser = new MavenParser();
    private IParser<String> gradleParser = new GradleParser();

    private ZipArchiveManager zipManager = new ZipArchiveManager();

    @Override
    //TODO: what is default project structure? Searches a config in root folder and in sub-folders.
    public Entry<String, String> parse(File project) {
        log.info("Parse {}", project);

        try (ZipFile zip = new ZipFile(project)) {
            final Optional<? extends ZipEntry> type = zip.stream()
                    .filter(nestedFilter())
                    .sorted(nestedComparator())
                    .filter(isMavenOrGradleEntry())
                    .findFirst();
            if (!type.isPresent()) failOnNotFound(project);

            log.info("In {} found \"{}\" config file", project, type.get().getName());
            final String config = zipManager.readEntry(zip, type.get());

            if (isMaven(type.get())) {
                return mavenParser.parse(config);
            } else {
                return gradleParser.parse(config);
            }
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }

    private Predicate<ZipEntry> nestedFilter() {
        return e -> !e.isDirectory() || getNesting(e) < 2;
    }

    private Comparator<ZipEntry> nestedComparator() {
        return comparingInt(this::getNesting);
    }

    private int getNesting(ZipEntry entry) {
        final String path = entry.getName();
        return StringUtils.countMatches(path, '/');
    }

    private Predicate<ZipEntry> isMavenOrGradleEntry() {
        return s -> s.getName().contains(MAVEN_CONFIGURATION) ||
                s.getName().contains(GRADLE_CONFIGURATION);
    }

    private boolean isMaven(ZipEntry config) {
        return config.getName().contains(MAVEN_CONFIGURATION);
    }

    private void failOnNotFound(File project) {
        throw new MetadataNotFoundException(project);
    }
}
