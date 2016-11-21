/**
 *
 */
package com.sam.jcc.cloud.persistence.project;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Data
@Entity
public class ProjectMetadataEntity {

    @Id
    private Long id;

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

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Dependency> dependencies;
}
