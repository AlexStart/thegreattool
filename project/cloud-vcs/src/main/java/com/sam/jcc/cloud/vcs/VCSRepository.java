package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.i.project.IStatusable;
import lombok.Data;

import java.io.File;

import static com.sam.jcc.cloud.i.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.INITIALIZED;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Data
public class VCSRepository implements IStatusable {

    private final static String REPOSITORY_PREFIX = getProperty("repository.prefix");

    private String groupId;
    private String artifactId;

    private File sources;

    private VCSRepositoryStatus status = INITIALIZED;

    public String getName() {
        return REPOSITORY_PREFIX + artifactId;
    }
}
