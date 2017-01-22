package com.sam.jcc.cloud.persistence.data;

import com.sam.jcc.cloud.exception.InternalCloudException;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
public class ProjectDataNotFoundException extends InternalCloudException {
    public ProjectDataNotFoundException(String projectName) {
        super("persistence.entity.notFound", projectName);
    }
}
