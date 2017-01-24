package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.exception.InternalCloudException;
import org.springframework.stereotype.Component;

/**
 * @author Alexey Zhytnik
 * @since 23-Jan-17
 */
@Component
class MongoDatabaseManager {

    public void create(AppData app) {
        throw new InternalCloudException();
    }

    public void drop(AppData app) {
        throw new InternalCloudException();
    }
}
