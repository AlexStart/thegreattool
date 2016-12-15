package com.sam.jcc.cloud.provider;

import com.sam.jcc.cloud.exception.BusinessCloudException;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
public class UnsupportedTypeException extends BusinessCloudException {

    public UnsupportedTypeException(Object item) {
        super("unsupported.item.type", item);
    }
}
