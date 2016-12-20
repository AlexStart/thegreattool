package com.sam.jcc.cloud.ci;

import lombok.Data;

import java.io.File;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
@Data
public class CIProject {

    private File sources;

    private String groupId;
    private String artifactId;

    public String getName() {
        return artifactId;
    }
}
