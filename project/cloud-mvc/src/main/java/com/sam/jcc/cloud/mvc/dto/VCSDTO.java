/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class VCSDTO extends BaseDTO {

	private String name;

	private String description;
	
	private String vcs;

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

	@Override
	public boolean isDisabled() {
		return false;
	}

	@Override
	public void setDisabled(boolean disabled) {
	}

	public String getVcs() {
		return vcs;
	}

	public void setVcs(String vcs) {
		this.vcs = vcs;
	}

	
}
