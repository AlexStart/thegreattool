package com.sam.jcc.cloud.exception;

import com.sam.jcc.cloud.i.Experimental;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Experimental
public class NotSupportedOperationException extends BusinessCloudException {
    public NotSupportedOperationException() {
        super("business.error.notSupported");
    }
}
