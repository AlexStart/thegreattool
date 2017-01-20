/**
 * 
 */
package com.sam.jcc.cloud.rules.service.impl.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.app.AppProvider;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.project.ProjectProvider;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 * 
 *         TODO
 *
 */
@Service
public class ProjectProviderService implements IService<IProjectMetadata> {

	@Autowired
	private AppProvider appProvider;

	@Autowired
	private List<ProjectProvider> projectProviders;

	@Override
	public IProjectMetadata create(IProjectMetadata projectMetadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProjectMetadata read(IProjectMetadata projectMetadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProjectMetadata update(IProjectMetadata projectMetadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(IProjectMetadata projectMetadata) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<? super IProjectMetadata> findAll() {
		List<? super IProjectMetadata> projects = new ArrayList<>();

		// 1. Iterate through providers
		Map<String, IProjectMetadata> projectsNamesToMetadata = new HashMap<>();
		for (ProjectProvider projectProvider : projectProviders) {
			for (IProjectMetadata projectMetadata : projectProvider.findAll()) {
				projectsNamesToMetadata.put(projectMetadata.getName(), projectMetadata);
			}
		}

		// 2. Iterate through all apps
		for (Object o : appProvider.findAll()) {
			IAppMetadata appMetadata = (IAppMetadata) o;
			if (projectsNamesToMetadata.get(appMetadata.getProjectName()) != null) {
				projects.add(projectsNamesToMetadata.get(appMetadata.getProjectName()));
			} else {
				IProjectMetadata emptyMetadata = new IProjectMetadata() {

					@Override
					public boolean hasSources() {
						return false;
					}

					@Override
					public String getName() {
						return appMetadata.getProjectName();
					}

				};
				projects.add(emptyMetadata);
			}
		}

		return projects;
	}

	@Override
	public IProjectMetadata create(Map<String, String> props) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProjectMetadata update(Map<String, String> props) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void findAndDelete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

}
