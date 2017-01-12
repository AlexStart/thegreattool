package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.i.vcs.IVCSMetadata;

/**
 * @author olegk
 * @since Jan, 11, 2016
 */
public class VCSRepositoryMetadata implements IVCSMetadata {

	private String name;

	private String description;

	public VCSRepositoryMetadata() {
	}

	public VCSRepositoryMetadata(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
