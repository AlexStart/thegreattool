package com.sam.jcc.cloud.dataprovider;

import com.sam.jcc.cloud.exception.BusinessCloudException;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
public class ProjectSourcesNotFound extends BusinessCloudException {
    public ProjectSourcesNotFound(AppData app) {
        super("persistence.sources.notFound", app);
    }
}
