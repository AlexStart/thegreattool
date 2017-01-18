package com.sam.jcc.cloud.mvc.controller.api.exception;

import com.sam.jcc.cloud.exception.InternalCloudException;

/**
 * @author Alexey Zhytnik
 * @since 12-Jan-17
 */
public class MetadataNotFoundException extends InternalCloudException {
    public MetadataNotFoundException() {
        super("project.metadata.notFound");
    }
}