package com.sam.jcc.cloud.app;

import com.sam.jcc.cloud.i.app.IAppMetadata;
import org.springframework.stereotype.Component;

/**
 * @author Alexey Zhytnik
 * @since 11.01.2017
 */
@Component
class AppMetadataValidator {

    private static final String ARTIFACT_REGEX = "[A-Za-z0-9_\\-.]+";

    public void validate(IAppMetadata app) {
        if (!isValidName(app)) {
            throw new AppMetadataValidationException(app.getProjectName());
        }
    }

    private boolean isValidName(IAppMetadata app) {
        return app.getProjectName().matches(ARTIFACT_REGEX);
    }
}
