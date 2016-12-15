package com.sam.jcc.cloud.exception;

/**
 * @author Alexey Zhytnik
 * @since 17.11.2016
 */
public class InternalCloudException extends CloudException {

    @Deprecated
    public InternalCloudException(String message) {
        super(message);
    }

    public InternalCloudException(Throwable cause) {
        super(cause, "internal.error.default");
    }

    protected InternalCloudException(String key, Object... args) {
        super(key, args);
    }

    protected InternalCloudException(Throwable cause, String key, Object... args) {
        super(cause, key, args);
    }
}
