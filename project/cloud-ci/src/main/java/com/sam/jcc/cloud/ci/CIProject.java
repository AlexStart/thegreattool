package com.sam.jcc.cloud.ci;

import lombok.Data;

import java.io.File;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
@Data
public class CIProject {

    /**
     * Sets once time, because of a lot of logic depends on name of CIProject.
     */
    private final static String REPOSITORY_PREFIX = getProperty("ci.prefix");

    private File sources;

    private String groupId;
    private String artifactId;

    public String getName() {
        return REPOSITORY_PREFIX + artifactId;
    }
}
