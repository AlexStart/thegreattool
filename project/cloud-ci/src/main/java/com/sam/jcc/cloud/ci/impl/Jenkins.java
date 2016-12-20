package com.sam.jcc.cloud.ci.impl;

import com.google.common.annotations.VisibleForTesting;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Artifact;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.CIProjectStatus;
import com.sam.jcc.cloud.ci.CIServer;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIException;
import com.sam.jcc.cloud.ci.exception.CIProjectNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIServerNotAvailableException;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.google.common.collect.Maps.immutableEntry;
import static com.google.common.collect.Sets.newHashSet;
import static com.offbytwo.jenkins.model.Build.BUILD_HAS_NEVER_RAN;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.util.Objects.isNull;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
public class Jenkins implements CIServer {

    @VisibleForTesting
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PACKAGE)
    private JenkinsServer server;

    @VisibleForTesting
    @Getter(AccessLevel.PACKAGE)
    private ItemStorage<CIProject> workspace;

    private JenkinsConfigurationBuilder builder;
    private JenkinsProjectStatusManager statusManager;

    public Jenkins() {
        server = new JenkinsServer(
                URI.create(getProperty("ci.host")),
                getProperty("ci.user"),
                getProperty("ci.password")
        );

        if (!server.isRunning()) {
            throw new CIServerNotAvailableException();
        }

        installRequiredPlugins();

        workspace = new ItemStorage<>(CIProject::getName);
        workspace.setRoot(new File(getProperty("ci.workspace.folder")));

        builder = new JenkinsConfigurationBuilder(workspace);
        statusManager = new JenkinsProjectStatusManager(server);
    }

    @SuppressWarnings("unchecked")
    //TODO: maybe transfer to top level as separated feature
    private void installRequiredPlugins() {
        new JenkinsPluginInstaller(server)
                .install(newHashSet(
                        immutableEntry("copyartifact", "1.38.1"),
                        immutableEntry("copy-data-to-workspace-plugin", "1.0")
                ));
    }

    @Override
    public void create(CIProject project) {
        try {
            workspace.create(project);
            updateSources(project);
            final String config = builder.build(project);
            server.createJob(project.getName(), config, true);
        } catch (IOException e) {
            workspace.delete(project);
            throw new CIException(e);
        }
    }

    @Override
    public void build(CIProject project) {
        final JobWithDetails job = loadJob(project);
        updateSources(project);
        try {
            job.build(true);
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    private void updateSources(CIProject project) {
        final FileManager files = new FileManager();
        final File src = workspace.get(project);

        files.cleanDir(src);
        files.copyDir(project.getSources(), src);
    }

    @Override
    public InputStream getBuild(CIProject project) {
        final Build build = loadJob(project).getLastBuild();

        if (build.equals(BUILD_HAS_NEVER_RAN)) {
            throw new CIBuildNotFoundException(project);
        }
        try {
            final BuildWithDetails details = build.details();
            final Optional<Artifact> artifact = details
                    .getArtifacts()
                    .stream()
                    .findFirst();

            if (!artifact.isPresent()) {
                throw new CIBuildNotFoundException(project);
            }
            return details.downloadArtifact(artifact.get());
        } catch (IOException | URISyntaxException e) {
            throw new CIException(e);
        }
    }

    @Override
    public void delete(CIProject project) {
        loadJob(project);
        try {
            server.deleteJob(project.getName(), true);
            workspace.delete(project);
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    @Override
    public CIProjectStatus getStatus(CIProject project) {
        return statusManager.getStatus(project);
    }

    private JobWithDetails loadJob(CIProject project) {
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

    @VisibleForTesting void setWorkspace(File workspace) {
        this.workspace.setRoot(workspace);
    }
}
