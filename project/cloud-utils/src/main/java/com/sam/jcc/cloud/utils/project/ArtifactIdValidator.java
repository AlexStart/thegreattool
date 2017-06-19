package com.sam.jcc.cloud.utils.project;

import static java.util.Objects.isNull;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.exception.BusinessCloudException;

/**
 * @author Alexey Zhytnik
 * @since 11.01.2017
 */
@Component
public class ArtifactIdValidator {

	public void validate(String s) {
		if (!ProjectPackageHelper.isProjectNameValid(s)) {
			throw new AppMetadataValidationException();
		}
	}

	public static class AppMetadataValidationException extends BusinessCloudException {
		public AppMetadataValidationException() {
			super("project.artifactId.notValid");
		}
	}
}
