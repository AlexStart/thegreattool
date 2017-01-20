package com.sam.jcc.cloud.i.project;

import com.sam.jcc.cloud.i.IProvider;

public interface IProjectProvider extends IProvider<IProjectMetadata> {
	
    /**
     * Returns a name-identifier of IProjectMetadata in format: "{groupId}:{artifactId}".
     */
    String getName(IProjectMetadata metadata);
}
