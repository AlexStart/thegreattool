/**
 *
 */
package com.sam.jcc.cloud.persistence.project;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Data
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"groupId", "artifactId"})
)
@Entity
public class ProjectMetadataEntity {

    @Id
    @GeneratedValue
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