/**
 * 
 */
package com.sam.jcc.cloud.project.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.project.ProjectMetadata;
import com.sam.jcc.cloud.project.ProjectProvider;

/**
 * @author olegk
 *
 */
@Component
public class MavenProjectProvider extends ProjectProvider implements IHealth {

	private static final long MAVEN_PROJECT_PROVIDER_ID = 1L;

	public MavenProjectProvider(List<IEventManager<IProjectMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public boolean supports(IProjectMetadata metadata) {
		if (!(metadata instanceof ProjectMetadata))
			return false;

		final ProjectMetadata m = (ProjectMetadata) metadata;
		final String name = m.getProjectType();
		return name != null && name.equals("maven-project");
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
					String exec = SystemUtils.IS_OS_WINDOWS ? "mvn.bat -version" : "mvn -version";
					Process proc = rt.exec(exec);
					InputStream is = proc.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = br.readLine()) != null)
						sb.append(line).append("\n");
					proc.waitFor();
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
				return MAVEN_PROJECT_PROVIDER_ID;
			}

		};
	}

	@Override
	public Long getId() {
		return MAVEN_PROJECT_PROVIDER_ID;
	}

}
