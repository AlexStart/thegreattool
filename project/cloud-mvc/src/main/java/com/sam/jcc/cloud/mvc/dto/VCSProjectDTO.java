/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class VCSProjectDTO extends ProjectDTO {

	public boolean isDisabled() {
		return !isHasSources();
	}

	public void setDisabled(boolean disabled) {
		setHasSources(!disabled);
	}

}
