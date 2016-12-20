package com.sam.jcc.cloud.ci.exception;

import com.sam.jcc.cloud.ci.CIProject;

/**
 * @author Alexey Zhytnik
 * @since 19-Dec-16
 */
public class CIProjectNotFoundException extends CIException {

    public CIProjectNotFoundException(CIProject project) {
        super("ci.project.notFound", project);
    }
}
