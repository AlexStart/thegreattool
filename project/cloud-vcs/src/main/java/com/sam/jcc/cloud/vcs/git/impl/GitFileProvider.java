package com.sam.jcc.cloud.vcs.git.impl;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;

/**
 * @author olegk
 */
@Component
public class GitFileProvider extends VCSProvider implements IHealth {

	private static final long GIT_FILE_PROVIDER_ID = 3L;

	private static String PATH_2_REPO = getProperty("repository.base.folder");

	public GitFileProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	protected GitAbstractStorage getStorage() {
		return new GitFileStorage();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Long getId() {
		return GIT_FILE_PROVIDER_ID;
	}

	@Override
	public IHealthMetadata checkHealth() {
		return new IHealthMetadata() {

			@Override
			public String getHost() {
				return null;
			}

			@Override
			public String getPort() {
				return null;
			}

			@Override
			public String getUrl() {
				try {
					Runtime rt = Runtime.getRuntime();
					String exec = SystemUtils.IS_OS_WINDOWS ? "git --version" : "git --version";
					Process proc = rt.exec(exec);
					InputStream is = proc.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = br.readLine()) != null)
						sb.append(line).append("\n");
					proc.waitFor();
					//
					sb.append(GitFileProvider.PATH_2_REPO).append("\n");
					return sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public boolean isAlive() {
				return getUrl() != null;
			}

			@Override
			public String getName() {
				return getI18NDescription();
			}

			@Override
			public Long getId() {
				return GIT_FILE_PROVIDER_ID;
			}

		};
	}

	@Override
	public String getType() {
		return "git-file";
	}
}
