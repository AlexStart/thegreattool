package com.sam.jcc.cloud.exception;

/**
 * @author Alexey Zhytnik
 * @since 17.11.2016
 */
public class BusinessCloudException extends CloudException {

    public BusinessCloudException() {
        super();
    }

    public BusinessCloudException(String message) {
        super(message);
    }

    public BusinessCloudException(Throwable cause) {
        super(cause);
    }

    public BusinessCloudException(String message, Throwable cause) {
        super(message, cause);
    }
}
