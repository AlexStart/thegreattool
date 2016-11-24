package com.sam.jcc.cloud.i;

/**
 * @author Alexey Zhytnik
 * @since 16.11.2016
 */
public abstract class CloudException extends RuntimeException {

    public CloudException() {
        super();
    }

    public CloudException(String message) {
        super(message);
    }

    public CloudException(Throwable cause) {
        super(cause);
    }

    public CloudException(String message, Throwable cause) {
        super(message, cause);
    }
}
