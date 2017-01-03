package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.IStatusable;
import com.sam.jcc.cloud.i.project.IProjectMetadata;

import lombok.Data;

import java.io.File;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * @author Alec Kotovich
 */
@Data
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
    public String toString() {
        return toStringHelper(getClass())
                .add("name", projectName)
                .add("groupId", groupId)
                .add("artifactId", artifactId)
                .toString();
    }
}
