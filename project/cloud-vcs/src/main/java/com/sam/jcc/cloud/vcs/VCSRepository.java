package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.i.IStatusable;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import lombok.Data;
import lombok.ToString;

import java.io.File;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.INITIALIZED;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Data
@ToString(of = {"artifactId", "status"})
public class VCSRepository implements IVCSMetadata, IStatusable {

    /**
     * Sets once time, because of a lot of logic depends on name of VCSRepository.
     */
    private final static String REPOSITORY_PREFIX = getProperty("repository.prefix");

    private File sources;
    private String artifactId;
    private VCSRepositoryStatus status = INITIALIZED;

    public String getName() {
        return REPOSITORY_PREFIX + artifactId;
    }

    //TODO:
    @Override
    public void setName(String name) {
        // unused
    }

    //TODO:
    @Override
    public void setDescription(String description) {
        // unused
    }

    @Override
    public String getDescription() {
        return toString();
    }
}
