package com.sam.jcc.cloud.utils.project;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Resources;
import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.utils.project.DependencyManager.Dependency;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Charsets.UTF_8;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 13.01.2017
 */
class GradleDependencyManager implements IDependencyManager<Dependency> {

    private static final Pattern NAME_PATTERN, SCOPE_PATTER, GROUP_PATTERN,
            VERSION_PATTERN, DEPENDENCIES_PATTERN, SHORT_DEFINITION;

    static {
        SCOPE_PATTER = Pattern.compile("(.+)\\(|(\\S+)");
        NAME_PATTERN = Pattern.compile("name:\\s*'(.*?)'|name:\\s*\"(.*?)\"");
        GROUP_PATTERN = Pattern.compile("group:\\s*'(.*?)'|group:\\s*\"(.*?)\"");
        VERSION_PATTERN = Pattern.compile("version:\\s*'(.*?)'|version:\\s*\"(.*?)\"");
        DEPENDENCIES_PATTERN = Pattern.compile("dependencies\\s*\\{(((?!dependencies).|\\s)*?)}\\s*");
        SHORT_DEFINITION = Pattern.compile(".*\\([',\"](\\S+):(\\S+):(\\S+)[',\"]\\)|.*\\([',\"](\\S+):(\\S+)[',\"]\\)");
    }

    @Override
    public List<Dependency> getAllDependencies(File config) {
        final String dependencies = getDependenciesMatcher(config).group(1);
        return extractDependencies(dependencies);
    }

    @Override
    public String add(File config, Dependency dependency) {
        final Matcher matcher = getDependenciesMatcher(config);
        return matcher.replaceFirst(format("dependencies { $1 %s}", asString(dependency)));
    }

    private Matcher getDependenciesMatcher(File file) {
        final String build = asString(file);
        final Matcher matcher = DEPENDENCIES_PATTERN.matcher(build);

        if (!matcher.find()) {
            throw new InternalCloudException();
        }
        return matcher;
    }

    @VisibleForTesting List<Dependency> extractDependencies(String dependencies) {
        return Arrays
                .stream(dependencies.split("\t\n|\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(this::parse)
                .collect(toList());
    }

    private Dependency parse(String s) {
        final Dependency d = new Dependency();

        getNonNullGroup(SCOPE_PATTER, s).ifPresent(d::setScope);

        if (isShotDefinition(s)) {
            fillShort(d, s);
        } else {
            getNonNullGroup(GROUP_PATTERN, s).ifPresent(d::setGroupId);
            getNonNullGroup(NAME_PATTERN, s).ifPresent(d::setArtifactId);
            getNonNullGroup(VERSION_PATTERN, s).ifPresent(d::setVersion);
        }
        return d;
    }

    private void fillShort(Dependency d, String s) {
        final Matcher m = SHORT_DEFINITION.matcher(s);

        if (!m.find()) throw new InternalCloudException();

        if (nonNull(m.group(1))) {
            d.setGroupId(m.group(1));
            d.setArtifactId(m.group(2));
            d.setVersion(m.group(3));
        } else {
            d.setGroupId(m.group(4));
            d.setArtifactId(m.group(5));
        }
    }

    private boolean isShotDefinition(String def) {
        return def.endsWith(")");
    }

    private Optional<String> getNonNullGroup(Pattern pattern, String s) {
        final Matcher matcher = pattern.matcher(s);

        if (!matcher.find()) return empty();

        String group = matcher.group(1);
        if (isNull(group)) {
            group = matcher.group(2);
        }
        return ofNullable(group);
    }

    private String asString(Dependency dependency) {
        return format("\t%s group: \"%s\", name:\"%s\", version:\"%s\"\n",
                dependency.getScope(),
                dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getVersion()
        );
    }

    private String asString(File file) {
        try {
            final URL url = file.toURI().toURL();
            return Resources.toString(url, UTF_8);
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }
}
