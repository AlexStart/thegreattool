package com.sam.jcc.cloud.ci.jenkins;

import com.google.common.annotations.VisibleForTesting;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Artifact;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.sam.jcc.cloud.PropertyResolverHelper;
import com.sam.jcc.cloud.ci.CIBuildStatus;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.CIServer;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIException;
import com.sam.jcc.cloud.ci.exception.CIProjectAlreadyExistsException;
import com.sam.jcc.cloud.ci.jenkins.config.JenkinsConfigurationBuilder;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Maps.immutableEntry;
import static com.google.common.collect.Sets.newHashSet;
import static com.offbytwo.jenkins.model.Build.BUILD_HAS_NEVER_RAN;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.springframework.util.StreamUtils.copyToByteArray;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
@Component
//TODO[rfisenko 6/16/17]: remove dependency to 'root' workspace when bean creation and remove this scope
@Scope("prototype")
//@Profile("prod")
public class Jenkins implements CIServer, ApplicationContextAware {

    private ApplicationContext context;

    @VisibleForTesting
    @Getter(AccessLevel.PACKAGE)
    private JenkinsServer server;

    @VisibleForTesting
    @Getter(AccessLevel.PACKAGE)
    private ItemStorage<CIProject> workspace;

    private JenkinsJobManager jobManager;

    private boolean connected;

    public Jenkins() {
        init(defaultJenkinsServer(), defaultWorkspace());
    }

    @VisibleForTesting
    public Jenkins(JenkinsServer jenkins, File root) {
        init(jenkins, root);
    }

    private void init(JenkinsServer jenkins, File root) {
        server = jenkins;

        // Issue # 29 Make all system connections LAZY
        if (connected = server.isRunning()) {
            prepareJenkins(root);
        }
    }

    private void prepareJenkins(File root) {
        installRequiredPlugins();

        workspace = new ItemStorage<>(CIProject::getName, null);
        workspace.setRoot(root);

        jobManager = new JenkinsJobManager(server);
    }

    @SuppressWarnings("unchecked")
    // TODO: maybe transfer to top level as separated feature
    private void installRequiredPlugins() {
        new JenkinsPluginInstaller(server).install(newHashSet(immutableEntry("copyartifact", "1.38.1"), // TODO
                // hardcoded.
                // Move
                // to
                // the
                // properties
                immutableEntry("copy-data-to-workspace-plugin", "1.0") // TODO
                // hardcoded.
                // Move
                // to
                // the
                // properties
        ));
    }

    @Override
    public boolean isEnabled() {
        if (!server.isRunning() || !connected) {
            // Issue # 29 Make all system connections LAZY
            init(defaultJenkinsServer(), defaultWorkspace()); // reconnect
        }
        return server.isRunning();
    }

    @Override
    public void create(CIProject project) {
        failOnExist(project);
        try {
            workspace.create(project);
            copySourcesToCIRepo(project);
            final String config = createConfigBuilder().build(project);
            server.createJob(project.getName(), config, true);
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
        copySourcesToCIRepo(project);
        try {
            job.build(true);
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    /**
     * Copy sources from project to CI local repository
     * NOTE: Also it need for checking project as maven or gradle. Please change checking method before removing
     *
     * @param project project data
     */
    private void copySourcesToCIRepo(CIProject project) {
        final FileManager files = new FileManager();
        final File src = workspace.get(project);

        files.cleanDir(src);
        files.copyDir(project.getSources(), src);
    }

    @Override
    public byte[] getLastSuccessfulBuild(CIProject project) {
        final JobWithDetails job = jobManager.loadJob(project);
        final Build build = job.getLastSuccessfulBuild();

        if (build.equals(BUILD_HAS_NEVER_RAN)) {
            throw new CIBuildNotFoundException(project, getLastBuildStatus(project), getLog(job));
        }
        try {
            final BuildWithDetails details = build.details();
            final Optional<Artifact> artifact = details.getArtifacts().stream().findFirst();

            if (!artifact.isPresent()) {
                throw new CIBuildNotFoundException(project, getLastBuildStatus(project), getLog(job));
            }
            final InputStream stream = details.downloadArtifact(artifact.get());
            return getBytes(stream);
        } catch (IOException | URISyntaxException e) {
            throw new CIException(e);
        }
    }

    private String getLog(JobWithDetails job) {
        final Build build = job.getLastBuild();

        if (build.equals(BUILD_HAS_NEVER_RAN))
            return "";

        try {
            return build.details().getConsoleOutputText();
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    private byte[] getBytes(InputStream stream) throws IOException {
        try {
            return copyToByteArray(stream);
        } finally {
            closeQuietly(stream);
        }
    }

    @Override
    public void delete(CIProject project) {
        try {
            jobManager.loadJob(project);
            server.deleteJob(project.getName(), true);
            workspace.delete(project);
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    @Override
    public List<CIProject> getAllProjects() {
        return jobManager.loadAllManagedProjects();
    }

    @Override
    public CIBuildStatus getLastBuildStatus(CIProject project) {
        return jobManager.getBuildStatus(project);
    }

    private static File defaultWorkspace() {
        return new File(getProperty("ci.workspace.folder"));
    }

    public static JenkinsServer defaultJenkinsServer() {

        return new JenkinsServer(URI.create(getJenkinsUrl()));

        // TODO Commented because User Management is not implemented yet.

        // return new JenkinsServer(URI.create(getProperty("ci.jenkins.host")),
        // getProperty("ci.jenkins.user"),
        // getProperty("ci.jenkins.password"));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * Lazy initialization of {@link JenkinsConfigurationBuilder} for Spring container working
     * NOTE: don't use this method before calling JenkinsConfigurationBuilder#prepareJenkins()
     *
     * @return new instance of builder
     */
    public JenkinsConfigurationBuilder createConfigBuilder() {
        return context.getBean(JenkinsConfigurationBuilder.class, workspace);
    }

    private static String getJenkinsUrl(){
        return PropertyResolverHelper.
                getConnectionUrl(getProperty("ci.jenkins.protocol"), getProperty("ci.jenkins.host"),getProperty("ci.jenkins.port"));
    }
}
