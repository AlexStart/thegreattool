package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.exception.BusinessCloudException;
import com.sam.jcc.cloud.i.project.IProjectMetadata;

/**
 * @author Alexey Zhytnik
 * @since 30-Dec-16
 */
public class ProjectNotFoundException extends BusinessCloudException {

    public ProjectNotFoundException(IProjectMetadata metadata) {
        super("project.notFound", metadata);
    }
}
