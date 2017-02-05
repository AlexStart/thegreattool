/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class DbProjectDTO extends ProjectDTO {

	public boolean isDisabled() {
		return !isHasSources() || isHasDb();
	}

	public void setDisabled(boolean disabled) {
		setHasSources(!disabled);
		setHasDb(disabled);
	}

}
