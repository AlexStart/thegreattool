package com.sam.jcc.cloud.app;

import com.sam.jcc.cloud.exception.BusinessCloudException;

/**
 * @author Alexey Zhytnik
 * @since 11.01.2017
 */
public class AppMetadataValidationException extends BusinessCloudException {
    public AppMetadataValidationException() {
        super("app.metadata.notValid");
    }
}
