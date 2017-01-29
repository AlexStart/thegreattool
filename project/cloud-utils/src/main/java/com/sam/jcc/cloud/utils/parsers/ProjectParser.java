package com.sam.jcc.cloud.utils.parsers;

import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Comparator.comparingInt;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.filefilter.FileFileFilter.FILE;

/**
 * @author Alexey Zhytnik
 * @since 28-Nov-16
 */
//TODO(a bad part of the app): there's no spec for nesting of search
@Slf4j
@Setter
@Component
public class ProjectParser implements IParser<File> {

    private static final String MAVEN_CONFIGURATION = "pom.xml";
    private static final String GRADLE_CONFIGURATION = "build.gradle";

    private IParser<String> mavenParser = new MavenParser();
    private IParser<String> gradleParser = new GradleParser();

    private ZipArchiveManager zipManager = new ZipArchiveManager();

    //TODO: add test, searches only in root
    public boolean isMaven(File sources) {
        IOFileFilter mavenFilter = new NameFileFilter(MAVEN_CONFIGURATION);
        return listFiles(sources, mavenFilter, FILE).size() == 1;
    }

    public File getConfiguration(File sources) {
        final Collection<File> maven = listFiles(sources, new NameFileFilter(MAVEN_CONFIGURATION), FILE);
        if (maven.size() == 1) {
            return getOnlyElement(maven);
        }

        final Collection<File> gradle = listFiles(sources, new NameFileFilter(GRADLE_CONFIGURATION), FILE);
        if (gradle.size() == 1) {
            return getOnlyElement(gradle);
        }
        throw new InternalCloudException();
    }

    //TODO: works only with root
    public File getPropertiesFile(File sources) {
        return new File(sources, "src/main/resources/application.properties");
    }

    @Override
    //TODO: what is default project structure? Searches a config in root folder and in sub-folders.
    public Entry<String, String> parse(File project) {
        log.info("Parse {}", project);

        try (ZipFile zip = new ZipFile(project)) {
            final ZipEntry type = searchType(project, zip);

            log.info("In {} found \"{}\" config file", project, type.getName());
            final String config = zipManager.readEntry(zip, type);

            if (isMaven(type)) {
                return mavenParser.parse(config);
            } else {
                return gradleParser.parse(config);
            }
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }

    private ZipEntry searchType(File project, ZipFile zip) {
        final Optional<? extends ZipEntry> type = zip.stream()
                .filter(nestedFilter())
                .sorted(nestedComparator())
                .filter(isMavenOrGradleEntry())
                .findFirst();
        if (!type.isPresent()) failOnNotFound(project);
        return type.get();
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
