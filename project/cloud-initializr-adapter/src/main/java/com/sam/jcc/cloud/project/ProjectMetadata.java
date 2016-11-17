/**
 *
 */
package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.project.IStatusable;
import com.sam.jcc.cloud.i.project.Status;
import lombok.Data;

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

    private byte[] projectSources;

    private Status status;

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("project", projectName)
                .add("artifactId", artifactId)
                .add("groupId", groupId)
                .toString();
    }
}
