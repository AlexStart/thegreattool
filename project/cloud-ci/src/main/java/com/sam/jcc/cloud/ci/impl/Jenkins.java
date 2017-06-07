package com.sam.jcc.cloud.ci.impl;

import com.google.common.annotations.VisibleForTesting;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Artifact;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.sam.jcc.cloud.ci.CIBuildStatus;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.CIServer;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIException;
import com.sam.jcc.cloud.ci.exception.CIProjectAlreadyExistsException;
import com.sam.jcc.cloud.ci.exception.CIServerNotAvailableException;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.context.annotation.Profile;
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
@Profile("prod")
public class Jenkins implements CIServer {

	@VisibleForTesting
	@Getter(AccessLevel.PACKAGE)
	private JenkinsServer server;

	@VisibleForTesting
	@Getter(AccessLevel.PACKAGE)
	private ItemStorage<CIProject> workspace;

	private JenkinsJobManager jobManager;
	private JenkinsConfigurationBuilder configBuilder;

	// TODO(a bad part of the app): should be compliant with Spring
	public Jenkins() {
		this(defaultJenkinsServer(), defaultWorkspace());
	}

	public Jenkins(JenkinsServer jenkins, File root) {
		server = jenkins;

		if (!server.isRunning()) {
			throw new CIServerNotAvailableException();
		}

		installRequiredPlugins();

		workspace = new ItemStorage<>(CIProject::getName, null);
		workspace.setRoot(root);

		jobManager = new JenkinsJobManager(server);
		configBuilder = new JenkinsConfigurationBuilder(workspace);
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
		return server.isRunning();
	}

	@Override
	public void create(CIProject project) {
		failOnExist(project);
		try {
			workspace.create(project);
			copySourcesToCIRepo(project);
			final String config = configBuilder.build(project);
			server.createJob(project.getName(), config, true);
		} catch (IOException e) {
			workspace.delete(project);//TODO[rfisenko 6/7/17]: delete after gitlab implementation
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
	 * @deprecated method will deleted after gitlab implementation
	 */
	@Deprecated
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

		return new JenkinsServer(URI.create(getProperty("ci.jenkins.host")));

		// TODO Commented because User Management is not implemented yet.

//		return new JenkinsServer(URI.create(getProperty("ci.jenkins.host")), getProperty("ci.jenkins.user"),
//				getProperty("ci.jenkins.password"));
	}
}
