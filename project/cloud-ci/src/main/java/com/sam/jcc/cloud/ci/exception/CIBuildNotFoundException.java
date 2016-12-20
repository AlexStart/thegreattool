package com.sam.jcc.cloud.ci.exception;

import com.sam.jcc.cloud.ci.CIProject;

/**
 * @author Alexey Zhytnik
 * @since 19-Dec-16
 */
public class CIBuildNotFoundException extends CIException {

    public CIBuildNotFoundException(CIProject project) {
        super("ci.build.notFound", project);
    }
}
