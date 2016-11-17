package com.sam.jcc.cloud.project;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestResolver;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Component
class ProjectValidator {

    @Autowired
    private ProjectMetadataConverter converter;
    @Autowired
    private ProjectRequestResolver requestResolver;
    @Autowired
    private InitializrMetadataProvider metadataProvider;

    public void validate(ProjectMetadata metadata) {
        final ProjectRequest request = converter.convert(metadata);
        validate(request);
    }

    private void validate(ProjectRequest request) {
        InitializrMetadata metadata = metadataProvider.get();
        requestResolver.resolve(request, metadata);
    }
}
