package com.sam.jcc.cloud.ci.impl;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.sam.jcc.cloud.ci.CIBuildStatus;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.exception.CIException;
import com.sam.jcc.cloud.ci.exception.CIProjectNotFoundException;

import java.io.IOException;

import static com.offbytwo.jenkins.model.BuildResult.FAILURE;
import static com.offbytwo.jenkins.model.BuildResult.SUCCESS;
import static com.sam.jcc.cloud.ci.CIBuildStatus.FAILED;
import static com.sam.jcc.cloud.ci.CIBuildStatus.IN_PROGRESS;
import static com.sam.jcc.cloud.ci.CIBuildStatus.SUCCESSFUL;
import static com.sam.jcc.cloud.ci.CIBuildStatus.UNKNOWN;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
class JenkinsJobManager {

    private JenkinsServer server;

    public JenkinsJobManager(JenkinsServer server) {
        this.server = server;
    }

    public JobWithDetails loadJob(CIProject project) {
        try {
            final JobWithDetails job = server.getJob(project.getName());

            if (isNull(job)) {
                throw new CIProjectNotFoundException(project);
            }
            return job;
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    public CIBuildStatus getBuildStatus(CIProject project) {
        try {
            return getStatus(project);
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    private CIBuildStatus getStatus(CIProject project) throws IOException {
        final JobWithDetails job = loadJob(project);

        if (nonNull(job)) {
            if (isInProgress(job)) return IN_PROGRESS;
            if (hasLastResult(job, FAILURE)) return FAILED;
            if (hasLastResult(job, SUCCESS)) return SUCCESSFUL;
        }
        return UNKNOWN;
    }

    private boolean isInProgress(JobWithDetails job) throws IOException {
        return job.isInQueue() || isNull(getLastBuildResult(job));
    }

    private boolean hasLastResult(JobWithDetails job, BuildResult expected) throws IOException {
        final BuildResult result = getLastBuildResult(job);
        return result.equals(expected);
    }

    private BuildResult getLastBuildResult(JobWithDetails job) throws IOException {
        final Build lastBuild = job.getLastBuild();
        return lastBuild.details().getResult();
    }
}
