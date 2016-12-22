package com.sam.jcc.cloud.ci.impl;

import com.google.common.annotations.VisibleForTesting;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Artifact;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.sam.jcc.cloud.ci.CIBuildStatus;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.CIProjectStatus;
import com.sam.jcc.cloud.ci.CIServer;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIException;
import com.sam.jcc.cloud.ci.exception.CIProjectAlreadyExistsException;
import com.sam.jcc.cloud.ci.exception.CIServerNotAvailableException;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.ILoggable;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.immutableEntry;
import static com.google.common.collect.Sets.newHashSet;
import static com.offbytwo.jenkins.model.Build.BUILD_HAS_NEVER_RAN;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.ci.CIProjectStatus.CREATED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.DELETED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_NO_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.UPDATED;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
@Slf4j
@Component
public class Jenkins implements CIServer, ILoggable {

    @VisibleForTesting
    @Getter(AccessLevel.PACKAGE)
    private JenkinsServer server;

    @VisibleForTesting
    @Getter(AccessLevel.PACKAGE)
    private ItemStorage<CIProject> workspace;

    private JenkinsJobManager jobManager;
    private JenkinsConfigurationBuilder builder;

    @Setter
    @Autowired
    private List<IEventManager<CIProject>> eventManagers = newArrayList();

    public Jenkins() {
        this(defaultJenkinsServer(), defaultWorkspace());
    }

    public Jenkins(JenkinsServer jenkins, File root) {
        server = jenkins;

        if (!server.isRunning()) {
            throw new CIServerNotAvailableException();
        }

        installRequiredPlugins();

        workspace = new ItemStorage<>(CIProject::getName);
        workspace.setRoot(root);

        jobManager = new JenkinsJobManager(server);
        builder = new JenkinsConfigurationBuilder(workspace);
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
        failOnExist(project);
        try {
            workspace.create(project);
            updateSources(project);
            final String config = builder.build(project);
            server.createJob(project.getName(), config, true);
            updateStatus(project, CREATED);
        } catch (IOException e) {
            workspace.delete(project);
            throw new CIException(e);
        }
    }

    private void failOnExist(CIProject project) {
        if (jobManager.hasJob(project)) {
            throw new CIProjectAlreadyExistsException(project);
        }
    }

    @Override
    public void build(CIProject project) {
        final JobWithDetails job = jobManager.loadJob(project);
        updateSources(project);
        try {
            job.build(true);
        } catch (IOException e) {
            throw new CIException(e);
        }
        updateStatus(project, UPDATED);
    }

    private void updateSources(CIProject project) {
        final FileManager files = new FileManager();
        final File src = workspace.get(project);

        files.cleanDir(src);
        files.copyDir(project.getSources(), src);
    }

    @Override
    public InputStream getLastSuccessfulBuild(CIProject project) {
        final Build build = jobManager.loadJob(project).getLastSuccessfulBuild();

        if (build.equals(BUILD_HAS_NEVER_RAN)) {
            updateStatus(project, HAS_NO_BUILD);
            throw new CIBuildNotFoundException(project);
        }
        try {
            final BuildWithDetails details = build.details();
            final Optional<Artifact> artifact = details
                    .getArtifacts()
                    .stream()
                    .findFirst();

            if (!artifact.isPresent()) {
                updateStatus(project, HAS_NO_BUILD);
                throw new CIBuildNotFoundException(project);
            }
            final InputStream stream = details.downloadArtifact(artifact.get());
            updateStatus(project, HAS_BUILD);
            return stream;
        } catch (IOException | URISyntaxException e) {
            throw new CIException(e);
        }
    }

    @Override
    public void delete(CIProject project) {
        try {
            jobManager.loadJob(project);
            server.deleteJob(project.getName(), true);
            workspace.delete(project);
            updateStatus(project, DELETED);
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    @Override
    public CIBuildStatus getLastBuildStatus(CIProject project) {
        return jobManager.getBuildStatus(project);
    }

    private void updateStatus(CIProject project, CIProjectStatus status) {
        project.setStatus(status);
        eventManagers.forEach(manager -> manager.fireEvent(project, this));
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    private static File defaultWorkspace() {
        return new File(getProperty("ci.workspace.folder"));
    }

    public static JenkinsServer defaultJenkinsServer() {
        return new JenkinsServer(
                URI.create(getProperty("ci.host")),
                getProperty("ci.user"),
                getProperty("ci.password")
        );
    }
}
