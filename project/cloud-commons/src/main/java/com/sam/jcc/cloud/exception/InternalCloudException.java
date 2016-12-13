package com.sam.jcc.cloud.exception;

/**
 * @author Alexey Zhytnik
 * @since 17.11.2016
 */
public class InternalCloudException extends CloudException {

    public InternalCloudException() {
        super();
    }

    public InternalCloudException(String message) {
        super(message);
    }

    public InternalCloudException(Throwable cause) {
        super(cause);
    }

    public InternalCloudException(String message, Throwable cause) {
        super(message, cause);
    }
}
