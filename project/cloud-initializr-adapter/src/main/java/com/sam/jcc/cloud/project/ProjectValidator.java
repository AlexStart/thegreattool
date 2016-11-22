package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.BusinessCloudException;
import com.sam.jcc.cloud.project.util.MetadataToRequestConverter;
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
    private MetadataToRequestConverter converter;
    @Autowired
    private ProjectRequestResolver requestResolver;
    @Autowired
    private InitializrMetadataProvider metadataProvider;

    public void validate(ProjectMetadata metadata) {
        final ProjectRequest request = converter.convert(metadata);
        validate(request);
    }

    //TODO: always throws business exception on all fail-cases
    private void validate(ProjectRequest request) {
        try {
            final InitializrMetadata metadata = metadataProvider.get();
            requestResolver.resolve(request, metadata);
        } catch (Exception e) {
            throw new BusinessCloudException(e);
        }
    }
}
