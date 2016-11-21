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
class SourceGenerator {

    @Autowired
    private ProjectGenerator generator;

    @Autowired
    private ProjectMetadataConverter converter;

    public File generate(ProjectMetadata project) {
        final ProjectRequest request = converter.convert(project);
        return generator.generateProjectStructure(request);
    }
}
