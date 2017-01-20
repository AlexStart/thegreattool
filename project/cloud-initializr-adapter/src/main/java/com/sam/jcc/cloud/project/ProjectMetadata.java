package com.sam.jcc.cloud.project;

import java.io.File;
import java.util.List;

import com.sam.jcc.cloud.i.IStatusable;
import com.sam.jcc.cloud.i.project.IProjectMetadata;

import lombok.Data;
import lombok.ToString;

/**
 * @author Alec Kotovich
 */
@Data
@ToString(of = { "projectName", "groupId", "artifactId" })
public class ProjectMetadata implements IProjectMetadata, IStatusable {

	private String bootVersion;

	private String projectName;
	private String projectType;
	private String javaVersion;
	private Boolean webAppPackaging;

	private String groupId;
	private String artifactId;
	private String version;
	private String description;

	private String basePackage;

	private List<String> dependencies;

	private File directory;
	private byte[] projectSources;

	private ProjectStatus status;

	private Long id;

	@Override
	public boolean hasSources() {
		return projectSources != null;
	}

	@Override
	public String getName() {
		return projectName;
	}
}
