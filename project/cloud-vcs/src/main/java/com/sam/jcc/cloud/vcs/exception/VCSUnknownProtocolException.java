package com.sam.jcc.cloud.vcs.exception;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
public class VCSUnknownProtocolException extends VCSException {

    public VCSUnknownProtocolException(String protocol) {
        super("vcs.protocol.unknown", protocol);
    }
}
