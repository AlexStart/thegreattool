package com.sam.jcc.cloud.ci.exception;

import com.sam.jcc.cloud.ci.CIBuildStatus;
import com.sam.jcc.cloud.ci.CIProject;

import static com.sam.jcc.cloud.ci.CIBuildStatus.FAILED;
import static com.sam.jcc.cloud.ci.CIBuildStatus.IN_PROGRESS;
import static com.sam.jcc.cloud.ci.CIBuildStatus.SUCCESSFUL;

/**
 * @author Alexey Zhytnik
 * @since 19-Dec-16
 */
public class CIBuildNotFoundException extends CIException {

    public CIBuildNotFoundException(CIProject project, CIBuildStatus buildStatus, String log) {
        super("ci.build.notFound", project, toString(buildStatus), log);
    }

    private static String toString(CIBuildStatus status) {
        return translateAndFill(getTranslateKey(status));
    }

    //TODO: maybe create translatable elements or transfer keys to statuses
    private static String getTranslateKey(CIBuildStatus status) {
        if (status == FAILED) return "ci.build.status.failed";
        if (status == SUCCESSFUL) return "ci.build.status.successful";
        if (status == IN_PROGRESS) return "ci.build.status.inProgress";
        return "ci.build.status.unknown";
    }
}
