package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.exception.CloudException;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
public class VCSException extends CloudException {

    public VCSException(Throwable e) {
        super(e);
    }

    public VCSException(String message) {
        super(message);
    }
}
