package com.sam.jcc.cloud.utils.parsers;

import com.sam.jcc.cloud.i.BusinessCloudException;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.Integer.compare;

/**
 * @author Alexey Zhytnik
 * @since 28-Nov-16
 */
@Setter
public class ProjectParser implements IParser<File> {

    private static final String MAVEN_CONFIGURATION = "pom.xml";
    private static final String GRADLE_CONFIGURATION = "build.gradle";

    private IParser<String> mavenParser = new MavenParser();
    private IParser<String> gradleParser = new GradleParser();

    private ZipArchiveManager zipManager = new ZipArchiveManager();

    @Override
    public Entry<String, String> parse(File project) {
        final ZipFile zip = zipManager.getZipFile(project);

        final Optional<? extends ZipEntry> type = zip.stream()
                .filter(nestedFilter())
                .sorted(nestedComparator())
                .filter(isMavenOrGradleEntry())
                .findFirst();
        if (!type.isPresent()) failOnNotFound(project);

        final String config = zipManager.readEntry(zip, type.get());

        if (isMaven(type.get())) {
            return mavenParser.parse(config);
        }
        return gradleParser.parse(config);
    }

    private Predicate<ZipEntry> nestedFilter() {
        return e -> !e.isDirectory() || getNesting(e) < 2;
    }

    private Comparator<ZipEntry> nestedComparator() {
        return (a, b) -> compare(getNesting(a), getNesting(b));
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
        throw new BusinessCloudException("Unknown project type " + project);
    }
}
