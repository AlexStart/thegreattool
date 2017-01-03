package com.sam.jcc.cloud.ci.exception;

/**
 * @author Alexey Zhytnik
 * @since 19-Dec-16
 */
public class CIServerNotAvailableException extends CIException {

    public CIServerNotAvailableException() {
        super("ci.server.notAvailable");
    }
}
