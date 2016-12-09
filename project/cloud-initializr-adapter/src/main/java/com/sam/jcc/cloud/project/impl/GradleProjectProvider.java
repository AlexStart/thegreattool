/**
 * 
 */
package com.sam.jcc.cloud.project.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.project.ProjectMetadata;
import com.sam.jcc.cloud.project.ProjectProvider;

/**
 * @author olegk
 *
 */
@Component
public class GradleProjectProvider extends ProjectProvider {

	public GradleProjectProvider(List<IEventManager<IProjectMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public boolean supports(IProjectMetadata metadata) {
		if (!(metadata instanceof ProjectMetadata))
			return false;

		final ProjectMetadata m = (ProjectMetadata) metadata;
		final String name = m.getProjectType();
		return name != null && name.equals("gradle-project");
	}

}
