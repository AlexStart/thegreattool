package com.sam.jcc.cloud.vcs;

import lombok.Data;

import java.io.File;

import static com.sam.jcc.cloud.i.PropertyResolver.getProperty;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Data
public class VCSRepository {

    private final static String REPOSITORY_PREFIX = getProperty("repository.prefix");

    private String groupId;
    private String artifactId;

    private File sources;

    public String getName() {
        return REPOSITORY_PREFIX + artifactId;
    }
}
