package com.sam.jcc.cloud.ci;

import java.io.InputStream;
import java.util.List;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
public interface CIServer {

    boolean isEnabled();

    /**
     * Creates a build-task of CIProject,
     * copies its sources into the private server workspace.
     */
    void create(CIProject project);

    /**
     * Activates a build-task of CIProject with latest sources.
     */
    void build(CIProject project);

    /**
     * Deletes CIProject build-task and its sources.
     */
    void delete(CIProject project);

    InputStream getLastSuccessfulBuild(CIProject project);

    CIBuildStatus getLastBuildStatus(CIProject project);

    List<CIProject> getAllProjects();
}
