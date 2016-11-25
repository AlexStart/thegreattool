package com.sam.jcc.cloud.project;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Component
class SourceProcessor {

    @Autowired
    private ProjectGenerator generator;

    @Autowired
    private MetadataToRequestConverter converter;

    public void process(ProjectMetadata project) {
        final ProjectRequest request = converter.convert(project);
        File srcDir = generator.generateProjectStructure(request);
        project.setDirectory(srcDir);
    }
}