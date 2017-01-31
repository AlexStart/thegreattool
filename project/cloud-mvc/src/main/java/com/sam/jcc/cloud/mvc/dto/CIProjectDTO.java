/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class CIProjectDTO extends ProjectDTO {

	public boolean isDisabled() {
		return !isHasSources() || isHasCI();
	}

	public void setDisabled(boolean disabled) {
		setHasSources(!disabled);
		setHasCI(disabled);
	}

}
