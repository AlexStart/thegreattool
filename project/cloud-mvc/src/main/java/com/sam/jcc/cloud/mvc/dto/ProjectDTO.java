/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class ProjectDTO extends AppDTO {
	
	private Long providerId;

	private boolean hasSources;
	
	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public boolean isHasSources() {
		return hasSources;
	}

	public void setHasSources(boolean hasSources) {
		this.hasSources = hasSources;
	}

	public boolean isDisabled() {
		return isHasSources();
	}

	public void setDisabled(boolean disabled) {
		setHasSources(disabled);
	}

}
