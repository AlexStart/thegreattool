/**
 * 
 */
package com.sam.jcc.cloud.rules.service.impl.provider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.data.IDataProvider;
import com.sam.jcc.cloud.project.ProjectMetadata;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
import com.sam.jcc.cloud.rules.service.IService;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;

/**
 * @author olegk
 * 
 *         TODO
 *
 */
@Service
public class DbProviderService implements IService<IDataMetadata> {

	@Autowired
	private List<IDataProvider> dbProviders;

	@Autowired
	private ProjectProviderService projectProviderService;

	@Autowired
	private FileManager files;

	@Autowired
	private ZipArchiveManager zipManager;

	@Override
	public IDataMetadata create(IDataMetadata t) {
		throw new UnsupportedCallException();
	}

	@Override
	public IDataMetadata read(IDataMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataMetadata update(IDataMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(IDataMetadata t) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<? super IDataMetadata> findAll() {
		List<? super IDataMetadata> dbs = new ArrayList<>();
		dbProviders.forEach(dbProvider -> dbs.add(new IDataMetadata() {

			@Override
			public String getProviderName() {
				return dbProvider.getI18NName();
			}

			@Override
			public String getProviderDescription() {
				return dbProvider.getI18NDescription();
			}

			@Override
			public String getDbName() {
				return null;
			}

			@Override
			public void setDbName(String name) {
			}

		}));
		return dbs;
	}

	@Override
	public IDataMetadata create(Map<?, ?> props) {
		throw new UnsupportedCallException();
	}

	@Override
	public void delete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public IDataMetadata update(Map<?, ?> props) {
		Long projectId = (Long) props.get("id");
		ProjectMetadata projectMetadata = new ProjectMetadata();
		projectMetadata.setId(projectId);
		ProjectMetadata project = (ProjectMetadata) projectProviderService.read(projectMetadata);
		if (project == null || !project.hasSources()) {
			return null;
		}

		byte[] projectSources = project.getProjectSources();

		AppData appData = new AppData();
		appData.setAppName(project.getArtifactId());
		appData.setSources(projectSources);

		Long providerId = (Long) props.get("providerId");
		IDataProvider targetProvider = dbProviders.stream().filter(p -> p.getId().equals(providerId)).findAny()
				.orElse(null);
		if (targetProvider != null) {
			IDataMetadata updatedApp = targetProvider.update(appData);
			return updatedApp;
		}

		return null;
	}

	@Override
	public void findAndDelete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<Long, String> getNames() {
		dbProviders.sort((p1, p2) -> {
			if (p1 != null && p2 != null && p1.getId() != null && p2.getId() != null) {
				return p1.getId().compareTo(p2.getId());
			}
			return 0;
		});

		Map<Long, String> names = new LinkedHashMap<>();
		dbProviders.forEach(p -> names.put(p.getId(), p.getI18NName()));

		return names;
	}

}
