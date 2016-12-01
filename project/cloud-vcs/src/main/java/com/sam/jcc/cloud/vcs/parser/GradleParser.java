package com.sam.jcc.cloud.vcs.parser;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.vcs.VCSException;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 28-Nov-16
 */
class GradleParser implements IParser<String> {

    private Pattern groupPattern = Pattern.compile("group\\s*=?\\s*'(\\S*)'");
    private Pattern artifactPattern = Pattern.compile("artifact\\s*=?\\s*'(\\S*)'");
    private Pattern baseNamePattern = Pattern.compile("baseName\\s*=\\s*'(\\S*)'");

    @Override
    public Entry<String, String> parse(String build) {
        return new SimpleEntry<>(parseGroup(build), parseArtifact(build));
    }

    @VisibleForTesting String parseGroup(String build) {
        final Matcher matcher = groupPattern.matcher(build);
        return matcher.find() ? matcher.group(1) : "";
    }

    @VisibleForTesting String parseArtifact(String build) {
        Matcher baseName = baseNamePattern.matcher(build);
        if (baseName.find()) return baseName.group(1);

        Matcher artifact = artifactPattern.matcher(build);
        if (artifact.find()) return artifact.group(1);

        throw new VCSException(format("ArtifactId not found! build.gradle = \"{0}\" ", build));
    }
}
