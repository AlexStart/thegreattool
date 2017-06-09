/**
 * 
 */
package com.sam.jcc.cloud.vcs.git.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.PropertyResolver;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.VCSRepository;

import lombok.Setter;

/**
 * @author olegk
 *
 */
@Component
public class GitlabProvider extends VCSProvider implements IHealth {

	private static final long GITLAB_PROVIDER_ID = 8L;
	public static final String TYPE = "gitlab";

	@Setter
	@Autowired
	@VisibleForTesting
	private GitMetadataDao dao;

	public GitlabProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
		super(eventManagers);
	}

	@PostConstruct
	public void setUp() {
		// TODO
	}

	@Override
	public Long getId() {
		return GITLAB_PROVIDER_ID;
	}

	@Override
	public boolean supports(IVCSMetadata metadata) {
		return metadata instanceof VCSRepository;
	}

	@Override
	public IVCSMetadata preprocess(IVCSMetadata metadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVCSMetadata process(IVCSMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVCSMetadata postprocess(IVCSMetadata metadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public IVCSMetadata read(IVCSMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVCSMetadata update(IVCSMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(IVCSMetadata t) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<? super IVCSMetadata> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GitAbstractStorage getStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHealthMetadata checkHealth() {
		return new IHealthMetadata() {

			@Override
			public Long getId() {
				return GITLAB_PROVIDER_ID;
			}

			@Override
			public String getName() {
				return getI18NDescription();
			}

			@Override
			public String getHost() {
				return PropertyResolver.getProperty("gitlab.remote.server.host");
			}

			@Override
			public String getPort() {
				return PropertyResolver.getProperty("gitlab.remote.server.port");
			}

			@Override
			public String getUrl() {
				return getHost() + ":" + getPort();
			}

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return false;
			}

		};

	}

}
