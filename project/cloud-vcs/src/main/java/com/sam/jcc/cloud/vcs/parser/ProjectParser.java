package com.sam.jcc.cloud.vcs.parser;

import com.sam.jcc.cloud.tool.ZipArchiveManager;
import lombok.Setter;

import java.io.File;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
                .filter(isMavenOrGradleEntry())
                .findFirst();
        if (!type.isPresent()) failOnNotFound(project);

        final String config = zipManager.readEntry(zip, type.get());

        if (isMaven(type.get())) {
            return mavenParser.parse(config);
        }
        return gradleParser.parse(config);
    }

    private Predicate<ZipEntry> isMavenOrGradleEntry() {
        return s -> s.getName().contains(MAVEN_CONFIGURATION) ||
                s.getName().contains(GRADLE_CONFIGURATION);
    }

    private boolean isMaven(ZipEntry config) {
        return config.getName().contains(MAVEN_CONFIGURATION);
    }

    private void failOnNotFound(File project) {
        throw new RuntimeException("Unknown project type " + project);
    }
}
