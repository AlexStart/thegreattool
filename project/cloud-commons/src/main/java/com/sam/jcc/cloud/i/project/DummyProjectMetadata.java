/**
 * 
 */
package com.sam.jcc.cloud.i.project;

import lombok.Data;

/**
 * @author olegk
 *
 */
@Data
public class DummyProjectMetadata implements IProjectMetadata {

	private Long id;
	private String name;
	private String type;
	private byte[] projectSources;
	private String ci;
	private String vcs;
	private String db;
	private String cQ;

	@Override
	public boolean hasSources() {
		return projectSources != null;
	}

	@Override
	public boolean hasVCS() {
		return vcs != null;
	}

	@Override
	public boolean hasCI() {
		return ci != null;
	}

	@Override
	public boolean hasDb() {
		return db != null;
	}

	@Override
	public boolean hasCQ() {
		return cQ != null;
	}

	public DummyProjectMetadata(Long id) {
		this.id = id;
	}

	public DummyProjectMetadata(Long id, String name) {
		this.id = id;
		this.name = name;
	}

}
