/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class CDProjectDTO extends ProjectDTO {

	public boolean isDisabled() {
		return !isHasSources() || isHasCI();
	}

	public void setDisabled(boolean disabled) {
		setHasSources(!disabled);
		setHasCI(disabled);
	}

}
