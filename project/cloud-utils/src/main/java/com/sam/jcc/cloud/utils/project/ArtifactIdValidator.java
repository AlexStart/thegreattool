package com.sam.jcc.cloud.utils.project;

import com.sam.jcc.cloud.exception.BusinessCloudException;
import org.springframework.stereotype.Component;

/**
 * @author Alexey Zhytnik
 * @since 11.01.2017
 */
@Component
public class ArtifactIdValidator {

    private static final String ARTIFACT_REGEX = "[A-Za-z0-9_\\-.]+";

    public void validate(String artifactId) {
        if (!artifactId.matches(ARTIFACT_REGEX)) {
            throw new AppMetadataValidationException();
        }
    }

    public static class AppMetadataValidationException extends BusinessCloudException {
        public AppMetadataValidationException() {
            super("project.artifactId.notValid");
        }
    }
}
