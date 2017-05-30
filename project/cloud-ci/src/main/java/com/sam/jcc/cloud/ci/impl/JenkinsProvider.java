package com.sam.jcc.cloud.ci.impl;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.ci.CIProjectStatus.CREATED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.DELETED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_NO_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.UPDATED;

import java.net.InetAddress;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.CIProjectStatus;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIProjectAlreadyExistsException;
import com.sam.jcc.cloud.ci.exception.CIServerNotAvailableException;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.i.ci.ICIProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.provider.UnsupportedTypeException;

import lombok.Setter;

/**
 * @author Alexey Zhytnik
 * @since 22-Dec-16
 */
@Component
public class JenkinsProvider extends AbstractProvider<ICIMetadata>implements ICIProvider, IHealth {

	private static final long JENKINS_PROVIDER_ID = 5L;

	@Setter
	@Autowired(required = false)
	@VisibleForTesting
	private Jenkins jenkins;

	@Setter
	@Autowired
	@VisibleForTesting
	private CIProjectDao dao;

	public JenkinsProvider(List<IEventManager<ICIMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public boolean supports(ICIMetadata m) {
		return m instanceof CIProject;
	}

	@Override
	public boolean isEnabled() {
		return jenkins.isEnabled();
	}

	@Override
	public ICIMetadata preprocess(ICIMetadata m) {
		final CIProject project = asCIProject(m);
		if (dao.exist(project)) {
			throw new CIProjectAlreadyExistsException(project);
		}
		return project;
	}

	@Override
	public ICIMetadata process(ICIMetadata m) {
		checkAccess();

		final CIProject project = asCIProject(m);
		jenkins.create(project);
		jenkins.build(project);
		return project;
	}

	@Override
	public ICIMetadata postprocess(ICIMetadata m) {
		dao.create(asCIProject(m));
		updateStatus(m, CREATED);
		return m;
	}

	@Override
	public ICIMetadata read(ICIMetadata m) {
		checkAccess();

		final CIProject project = asCIProject(m);
		try {
			final byte[] build = jenkins.getLastSuccessfulBuild(project);
			project.setBuild(build);
			updateStatus(project, HAS_BUILD);
		} catch (CIBuildNotFoundException e) {
			updateStatus(project, HAS_NO_BUILD);
			throw e;
		}
		return project;
	}

	@Override
	public ICIMetadata update(ICIMetadata m) {
		checkAccess();

		jenkins.build(asCIProject(m));
		updateStatus(m, UPDATED);
		return m;
	}

	@Override
	public void delete(ICIMetadata m) {
		checkAccess();
		final CIProject project = asCIProject(m);

		jenkins.delete(project);
		dao.delete(project);
		updateStatus(project, DELETED);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ICIMetadata> findAll() {
		checkAccess();
		return (List<ICIMetadata>) (List<?>) jenkins.getAllProjects();
	}

	private void checkAccess() {
		if (!jenkins.isEnabled()) {
			throw new CIServerNotAvailableException();
		}
	}

	private void updateStatus(ICIMetadata project, CIProjectStatus status) {
		asCIProject(project).setStatus(status);
		notify(project);
	}

	private CIProject asCIProject(ICIMetadata metadata) {
		if (!supports(metadata)) {
			throw new UnsupportedTypeException(metadata);
		}
		return (CIProject) metadata;
	}

	@Override
	public Long getId() {
		return JENKINS_PROVIDER_ID;
	}

	@Override
	public IHealthMetadata checkHealth() {
		return new IHealthMetadata() {

			private final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

			@Override
			public Long getId() {
				return JENKINS_PROVIDER_ID;
			}

			@Override
			public String getName() {
				return getI18NDescription();
			}

			@Override
			public String getHost() {
				try {
					URL url = new URL(getProperty("ci.jenkins.host"));
					return String.valueOf(url.getHost());
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public String getPort() {
				try {
					URL url = new URL(getProperty("ci.jenkins.host"));
					return String.valueOf(url.getPort());
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public String getUrl() {

				try {
					String host = getHost();
					Integer port = Integer.valueOf(getPort());
					// TODO hotfix
					URL apiUrl = new URL("http", host, port, getProperty("ci.jenkins.postfix") + "/api");
					HttpGet request = new HttpGet(apiUrl.toString());
					HttpResponse response = httpClient.execute(request);
					Validate.isTrue(response.getStatusLine().getStatusCode() == 200);
					String jenkinsVersion = response.getFirstHeader("X-Jenkins").getValue();
					StringBuilder sb = new StringBuilder();
					String hostName = host.equals("localhost") ? InetAddress.getLocalHost().getHostName() : host;
					URL url = new URL("http", hostName, port, "");
					sb.append(url.toString()).append("\n");
					sb.append("Jenkins: ");
					sb.append(jenkinsVersion);
					return sb.toString();

				} catch (Exception e) {
					try {
						String host = getHost();
						String hostName = host.equals("localhost") ? InetAddress.getLocalHost().getHostName() : host;
						URL url = new URL("http", hostName, Integer.valueOf(getPort()), "");
						return url.toString();
					} catch (Exception e2) {
						return null;
					}
				}

			}

			@Override
			public boolean isAlive() {
				try {
					URL url = new URL("http", getHost(), Integer.valueOf(getPort()), "/api");
					HttpGet request = new HttpGet(url.toString());
					HttpResponse response = httpClient.execute(request);
					return (response.getStatusLine().getStatusCode() == 200);
				} catch (Exception e) {
					return false;
				}
			}

		};
	}

	@Override
	public String getType() {
		return "jenkins";
	}
}
