package com.sam.jcc.cloud.exception;

/**
 * @author Alexey Zhytnik
 * @since 17.11.2016
 */
public class BusinessCloudException extends CloudException {

    public BusinessCloudException(Throwable cause) {
        super(cause, "business.error.default");
    }

    protected BusinessCloudException(String key, Object... args) {
        super(key, args);
    }

    protected BusinessCloudException(Throwable cause, String key, Object... args) {
        super(cause, key, args);
    }
}
