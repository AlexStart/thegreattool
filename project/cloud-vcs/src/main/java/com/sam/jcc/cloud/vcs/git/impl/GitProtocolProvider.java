package com.sam.jcc.cloud.vcs.git.impl;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;

/**
 * @author olegk
 */
@Component
public class GitProtocolProvider extends VCSProvider implements IHealth {

	private static final long GIT_PROTOCOL_PROVIDER_ID = 4L;

	public GitProtocolProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	protected GitAbstractStorage getStorage() {
		return new GitRemoteStorage();
	}

	// TODO: need check server
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Long getId() {
		return GIT_PROTOCOL_PROVIDER_ID;
	}

	@Override
	public IHealthMetadata checkHealth() {
		return new IHealthMetadata() {

			@Override
			public String getHost() {
				GitRemoteStorage remoteStorage = (GitRemoteStorage) getStorage();
				return remoteStorage.getHost();
			}

			@Override
			public String getPort() {
				GitRemoteStorage remoteStorage = (GitRemoteStorage) getStorage();
				return String.valueOf(remoteStorage.getPort());
			}

			@Override
			public String getUrl() {
				try {
					GitRemoteStorage remoteStorage = (GitRemoteStorage) getStorage();
					StringBuilder sb = new StringBuilder();
					String line = remoteStorage.getRepositoryURL();
					sb.append(line);
					return sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public boolean isAlive() {
				try {
					InetSocketAddress sa = new InetSocketAddress(getHost(), Integer.valueOf(getPort()));
					Socket ss = new Socket();
					ss.connect(sa, 500);
					ss.close();
					return true;
				} catch (Exception e) {
					return false;
				}

			}

			@Override
			public String getName() {
				return getI18NDescription();
			}

			@Override
			public Long getId() {
				return GIT_PROTOCOL_PROVIDER_ID;
			}

		};
	}

	@Override
	public String getType() {
		return "git-protocol";
	}
}
