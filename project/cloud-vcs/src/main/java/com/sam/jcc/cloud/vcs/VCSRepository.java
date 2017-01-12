package com.sam.jcc.cloud.vcs;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.INITIALIZED;

import java.io.File;

import com.sam.jcc.cloud.i.IStatusable;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;

import lombok.Data;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Data
public class VCSRepository implements IVCSMetadata, IStatusable {

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


	@Override
	public void setName(String name) {
		// unused
	}


	@Override
	public void setDescription(String description) {
		// unused		
	}


	@Override
	public String getDescription() {
		return toString();
	}
}
