package com.sam.jcc.cloud.project.util;

import com.sam.jcc.cloud.project.ProjectMetadata;
import io.spring.initializr.generator.ProjectRequest;
import org.springframework.stereotype.Component;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Component
public class MetadataToRequestConverter {

    public ProjectRequest convert(ProjectMetadata p) {
        final ProjectRequest r = new ProjectRequest();

        r.setLanguage("java");
        r.setName(p.getProjectName());
        r.setType(p.getProjectType());
        r.setGroupId(p.getGroupId());
        r.setVersion(p.getVersion());
        r.setStyle(p.getDependencies());
        r.setArtifactId(p.getArtifactId());
        r.setBootVersion(p.getBootVersion());
        r.setJavaVersion(p.getJavaVersion());
        r.setDescription(p.getDescription());
        r.setPackageName(p.getBasePackage());
        r.setPackaging(convertPackaging(p.getWebAppPackaging()));
        return r;
    }

    private String convertPackaging(boolean web) {
        return web ? "war" : "jar";
    }
}
