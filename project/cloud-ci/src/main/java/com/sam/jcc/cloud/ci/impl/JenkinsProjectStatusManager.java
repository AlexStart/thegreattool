package com.sam.jcc.cloud.ci.impl;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.ComputerSet;
import com.offbytwo.jenkins.model.Executor;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.CIProjectStatus;
import com.sam.jcc.cloud.ci.exception.CIException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.sam.jcc.cloud.ci.CIProjectStatus.COMPLETED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.FAILED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.IN_PROGRESS;
import static com.sam.jcc.cloud.ci.CIProjectStatus.UNREGISTERED;
import static java.util.Objects.nonNull;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
class JenkinsProjectStatusManager {

    private JenkinsServer server;

    public JenkinsProjectStatusManager(JenkinsServer server) {
        this.server = server;
    }

    public CIProjectStatus getStatus(CIProject project) {
        try {
            return getStatusNotSecured(project);
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    public CIProjectStatus getStatusNotSecured(CIProject project) throws IOException {
        if (exist(project)) {
            if (inProgress(project)) return IN_PROGRESS;
            if (isFailed(project)) return FAILED;
            if (isCompleted(project)) return COMPLETED;
        }
        return UNREGISTERED;
    }

    public boolean exist(CIProject project) throws IOException {
        return nonNull(loadJob(project));
    }

    private boolean inProgress(CIProject project) throws IOException {
        final JobWithDetails job = loadJob(project);

        final Optional<Executor> executor = getExecutors(server)
                .stream()
                .filter(byProjectExecutionFilter(project))
                .findFirst();

        return job.isInQueue() || executor.isPresent();
    }

    public boolean isFailed(CIProject project) throws IOException {
        final JobWithDetails job = loadJob(project);

        Build last = job.getLastBuild();
        Build failed = job.getLastUnsuccessfulBuild();
        return failed.equals(last);
    }

    public boolean isCompleted(CIProject project) throws IOException {
        final JobWithDetails job = loadJob(project);

        Build last = job.getLastBuild();
        Build failed = job.getLastSuccessfulBuild();
        return failed.equals(last);
    }

    private JobWithDetails loadJob(CIProject project) throws IOException {
        return server.getJob(project.getName());
    }

    private List<Executor> getExecutors(JenkinsServer server) throws IOException {
        final ComputerSet set = server.getComputerSet();
        return set.getComputers().get(0).getExecutors();
    }

    private Predicate<Executor> byProjectExecutionFilter(CIProject project) {
        final String name = project.getName();

        return e -> nonNull(e.getCurrentExecutable()) &&
                e.getCurrentExecutable().getUrl().contains(name);
    }
}
