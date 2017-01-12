/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

/**
 * @author olegk
 *
 */
public class AppDTO extends BaseDTO {

	private String projectName;

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
