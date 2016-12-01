package com.sam.jcc.cloud.vcs;

import lombok.Data;

import java.io.File;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Data
public class VCSRepository {

    private String groupId;
    private String artifactId;

    private File sources;

    public String getName() {
        return "Project-" + artifactId;
    }
}
