package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.persistence.project.Dependency;
import com.sam.jcc.cloud.persistence.project.ProjectMetadataEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 22.11.2016
 */
@Component
class ProjectEntityConverter {

    public ProjectMetadata convert(ProjectMetadataEntity entity) {
        final ProjectMetadata m = new ProjectMetadata();

        m.setId(entity.getId());
        m.setBootVersion(entity.getBootVersion());
        m.setProjectName(entity.getProjectName());
        m.setProjectType(entity.getProjectType());
        m.setJavaVersion(entity.getJavaVersion());
        m.setWebAppPackaging(entity.getWebAppPackaging());
        m.setGroupId(entity.getGroupId());
        m.setArtifactId(entity.getArtifactId());
        m.setVersion(entity.getVersion());
        m.setDescription(entity.getDescription());
        m.setBasePackage(entity.getBasePackage());
        m.setDependencies(convertDependencies(entity.getDependencies()));
        return m;
    }

    private List<String> convertDependencies(List<Dependency> dependencies) {
        return dependencies.stream()
                .map(Dependency::getName)
                .collect(toList());
    }
}
