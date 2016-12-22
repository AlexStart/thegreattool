package com.sam.jcc.cloud.ci.exception;

import com.sam.jcc.cloud.ci.CIProject;

/**
 * @author Alexey Zhytnik
 * @since 22-Dec-16
 */
public class CIProjectAlreadyExistsException extends CIException {

    public CIProjectAlreadyExistsException(CIProject project) {
        super("ci.project.exist", project);
    }
}
