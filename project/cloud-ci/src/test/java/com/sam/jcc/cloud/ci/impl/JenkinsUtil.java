package com.sam.jcc.cloud.ci.impl;

import java.io.File;

import static com.sam.jcc.cloud.ci.impl.Jenkins.defaultJenkinsServer;

/**
 * @author Alexey Zhytnik
 * @since 22-Dec-16
 */
public final class JenkinsUtil {

    private JenkinsUtil() {
    }

    //TODO: maybe change to another Jenkins server
    public static Jenkins getJenkins(File workspace) {
        return new Jenkins(defaultJenkinsServer(), workspace);
    }
}
