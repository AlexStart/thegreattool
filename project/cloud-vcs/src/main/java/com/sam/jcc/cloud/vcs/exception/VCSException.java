package com.sam.jcc.cloud.vcs.exception;

import com.sam.jcc.cloud.exception.CloudException;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
public class VCSException extends CloudException {

    public VCSException(Throwable cause) {
        super("vcs.error.default", cause);
    }

    protected VCSException(String key, Object... args) {
        super(key, args);
    }

    protected VCSException(Throwable cause, String key, Object... args) {
        super(cause, key, args);
    }
}
