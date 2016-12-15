package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.exception.BusinessCloudException;
import com.sam.jcc.cloud.i.project.IProjectMetadata;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
public class ProjectValidationException extends BusinessCloudException {

    public ProjectValidationException(Throwable cause, IProjectMetadata metadata) {
        super(cause, "project.validation.error", metadata);
    }
}
