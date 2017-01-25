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
	
	private boolean hasVcs;
	
	private boolean hasCI;
	
	private boolean hasDb;
	
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

	public boolean isHasVcs() {
		return hasVcs;
	}

	public void setHasVcs(boolean hasVcs) {
		this.hasVcs = hasVcs;
	}

	public boolean isHasCI() {
		return hasCI;
	}

	public void setHasCI(boolean hasCI) {
		this.hasCI = hasCI;
	}

	public boolean isHasDb() {
		return hasDb;
	}

	public void setHasDb(boolean hasDb) {
		this.hasDb = hasDb;
	}

	
}
