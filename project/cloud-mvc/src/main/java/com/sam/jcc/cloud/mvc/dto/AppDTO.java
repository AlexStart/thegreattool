/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class AppDTO extends BaseDTO {

	private String projectName;

	private String projectType;

	private boolean disabled;

	private String ci;

	private String db;

	private String vcs;
	
	private String cQ;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setName(String name) {
		this.projectName = name;
	}

	public String getName() {
		return projectName;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getType() {
		return projectType;
	}

	public void setType(String projectType) {
		this.projectType = projectType;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getVcs() {
		return vcs;
	}

	public void setVcs(String vcs) {
		this.vcs = vcs;
	}

	public String getCQ() {
		return cQ;
	}

	public void setCQ(String cQ) {
		this.cQ = cQ;
	}
	
	
}
