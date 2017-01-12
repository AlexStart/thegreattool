package com.sam.jcc.cloud.provider;

import com.sam.jcc.cloud.exception.BusinessCloudException;

/**
 * @author olegk
 * @since Jan, 11, 2017
 * 
 * Business logic exception; thrown when a provider is not allowed to be called!
 */
public class UnsupportedCallException extends BusinessCloudException {

    public UnsupportedCallException() {
        super("unsupported.call");
    }
}
