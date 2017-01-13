package com.sam.jcc.cloud.mvc.controller.api.exception;

import com.sam.jcc.cloud.exception.CloudException;

/**
 * @author olegk
 */
public class ValidationCloudException extends CloudException {

	public ValidationCloudException() {
		super("internal.validation.error");
	}

	public ValidationCloudException(Throwable cause) {
		super(cause, "internal.validation.error");
	}

	protected ValidationCloudException(String key, Object... args) {
		super(key, args);
	}

	protected ValidationCloudException(Throwable cause, String key, Object... args) {
		super(cause, key, args);
	}
}
