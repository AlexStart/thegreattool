package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.i.IStatusable;
import lombok.Data;

import java.io.File;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.INITIALIZED;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Data
public class VCSRepository implements IStatusable {

    /**
     * Sets once time, because of a lot of logic depends on name of VCSRepository.
     */
    private final static String REPOSITORY_PREFIX = getProperty("repository.prefix");

    private String groupId;
    private String artifactId;

    private File sources;

    private VCSRepositoryStatus status = INITIALIZED;

    public String getName() {
        return REPOSITORY_PREFIX + artifactId;
    }


    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("name", getName())
                .add("status", status)
                .toString();
    }
}
