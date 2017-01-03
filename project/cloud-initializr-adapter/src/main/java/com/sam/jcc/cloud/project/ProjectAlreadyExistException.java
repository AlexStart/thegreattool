package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.i.project.IProjectMetadata;

/**
 * @author Alexey Zhytnik
 * @since 30-Dec-16
 */
public class ProjectAlreadyExistException extends InternalCloudException {

    public ProjectAlreadyExistException(IProjectMetadata metadata) {
        super("project.exist", metadata);
    }
}
