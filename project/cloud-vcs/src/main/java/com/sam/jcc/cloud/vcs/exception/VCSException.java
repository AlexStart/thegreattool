package com.sam.jcc.cloud.vcs.exception;

import com.sam.jcc.cloud.exception.CloudException;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
public class VCSException extends CloudException {

    public VCSException(Throwable cause) {
        super(cause, "vcs.error.default");
    }

    protected VCSException(String key, Object... args) {
        super(key, args);
    }
}
