package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.persistence.project.Dependency;
import com.sam.jcc.cloud.persistence.project.ProjectMetadataEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Alexey Zhytnik
 * @since 22.11.2016
 */
@Component
class ProjectMetadataConverter {

    public ProjectMetadataEntity convert(ProjectMetadata m) {
        final ProjectMetadataEntity e = new ProjectMetadataEntity();

        e.setId(m.getId());
        e.setBootVersion(m.getBootVersion());
        e.setProjectName(m.getProjectName());
        e.setProjectType(m.getProjectType());
        e.setJavaVersion(m.getJavaVersion());
        e.setWebAppPackaging(m.getWebAppPackaging());
        e.setGroupId(m.getGroupId());
        e.setArtifactId(m.getArtifactId());
        e.setVersion(m.getVersion());
        e.setDescription(m.getDescription());
        e.setBasePackage(m.getBasePackage());
        e.setDependencies(convertDependencies(m.getDependencies()));
        return e;
    }

    private List<Dependency> convertDependencies(List<String> dependencies) {
        final List<Dependency> result = newArrayList();

        for (String name : dependencies) {
            final Dependency dependency = new Dependency();
            dependency.setName(name);
            result.add(dependency);
        }
        return result;
    }
}
