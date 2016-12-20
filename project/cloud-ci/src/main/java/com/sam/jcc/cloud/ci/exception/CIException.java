package com.sam.jcc.cloud.ci.exception;

import com.sam.jcc.cloud.exception.CloudException;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
public class CIException extends CloudException {

    public CIException(Throwable cause) {
        super("ci.error.default", cause);
    }

    protected CIException(String key, Object... args) {
        super(key, args);
    }

    protected CIException(Throwable cause, String key, Object... args) {
        super(cause, key, args);
    }
}
