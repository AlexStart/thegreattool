/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class CQProjectDTO extends ProjectDTO {

	public boolean isDisabled() {
		return !isHasSources() || isHasCQ();
	}

	public void setDisabled(boolean disabled) {
		setHasSources(!disabled);
		setHasCQ(disabled);
	}

}
