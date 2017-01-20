/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class ProjectDTO extends AppDTO {

	private boolean hasSources;

	public boolean isHasSources() {
		return hasSources;
	}

	public void setHasSources(boolean hasSources) {
		this.hasSources = hasSources;
	}

	public boolean isDisabled() {
		return !isHasSources();
	}

	public void setDisabled(boolean disabled) {
		setHasSources(!disabled);
	}

}
