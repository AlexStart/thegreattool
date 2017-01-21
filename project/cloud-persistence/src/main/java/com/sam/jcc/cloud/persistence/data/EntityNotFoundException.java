package com.sam.jcc.cloud.persistence.data;

import com.sam.jcc.cloud.exception.InternalCloudException;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
//TODO: use as default not found exception in dao-layout
public class EntityNotFoundException extends InternalCloudException {
    public EntityNotFoundException(Object entity) {
        super("persistence.entity.notFound", entity);
    }
}
