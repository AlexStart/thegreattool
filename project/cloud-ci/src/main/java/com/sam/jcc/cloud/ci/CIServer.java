package com.sam.jcc.cloud.ci;

import java.io.InputStream;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
public interface CIServer {

    /**
     * Creates a build-task of CIProject,
     * copies its sources into the private server workspace.
     */
    void create(CIProject project);

    /**
     * Updates sources of CIProject in the server workspace,
     * activates a build-task of CIProject
     */
    void build(CIProject project);

    /**
     * Deletes a build-task of CIProject and
     * its sources from the server workspace.
     */
    void delete(CIProject project);

    InputStream getLastSuccessfulBuild(CIProject project);

    CIBuildStatus getLastBuildStatus(CIProject project);
}
