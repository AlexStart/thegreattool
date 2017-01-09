/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class AppDTO {

	private Long id;

	private String projectName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

}
