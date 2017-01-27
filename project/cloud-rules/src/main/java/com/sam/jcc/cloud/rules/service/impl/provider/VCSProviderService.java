/**
 * 
 */
package com.sam.jcc.cloud.rules.service.impl.provider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.io.Files;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.project.ProjectMetadata;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
import com.sam.jcc.cloud.rules.service.IService;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSRepositoryMetadata;
import com.sam.jcc.cloud.vcs.git.impl.VCSProvider;

/**
 * @author olegk
 * 
 *         TODO
 *
 */
@Service
public class VCSProviderService implements IService<IVCSMetadata> {

	@Autowired
	private List<VCSProvider> vcsProviders;

	@Autowired
	private ProjectProviderService projectProviderService;

	@Autowired
	private FileManager files;

	@Autowired
	private ZipArchiveManager zipManager;

	@Override
	public IVCSMetadata create(IVCSMetadata t) {
		throw new UnsupportedCallException();
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
		List<? super IVCSMetadata> vcss = new ArrayList<>();
		vcsProviders.forEach(vcsProvider -> vcss
				.add(new VCSRepositoryMetadata(vcsProvider.getI18NName(), vcsProvider.getI18NDescription())));
		return vcss;
	}

	@Override
	public IVCSMetadata create(Map<?, ?> props) {
		throw new UnsupportedCallException();
	}

	@Override
	public void delete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public IVCSMetadata update(Map<?, ?> props) {
		Long projectId = (Long) props.get("id");
		ProjectMetadata projectMetadata = new ProjectMetadata();
		projectMetadata.setId(projectId);
		ProjectMetadata project = (ProjectMetadata) projectProviderService.read(projectMetadata);
		if (project == null || !project.hasSources()) {
			return null;
		}
		
		byte[] projectSources = project.getProjectSources();

		File tempZip;
		try {
			tempZip = File.createTempFile(project.getArtifactId() + "-" + new Random().nextInt(10000), ".zip");
		} catch (IOException e) {
			return null;
		}
		File tempDir = Files.createTempDir();
		files.write(projectSources, tempZip);
		zipManager.unzip(tempZip, tempDir);

		Long providerId = (Long) props.get("providerId");
		VCSProvider targetProvider = vcsProviders.stream().filter(p -> p.getId().equals(providerId)).findAny()
				.orElse(null);
		if (targetProvider != null) {
			final VCSRepository repo = new VCSRepository();
			repo.setArtifactId((String) props.get("projectName"));
			repo.setSources(tempDir);
			targetProvider.create(repo); // create empty repo
			return targetProvider.update(repo); // update with sources
		}
		return null;
	}

	@Override
	public void findAndDelete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<Long, String> getNames() {
		vcsProviders.sort((p1, p2) -> {
			if (p1 != null && p2 != null && p1.getId() != null && p2.getId() != null) {
				return p1.getId().compareTo(p2.getId());
			}
			return 0;
		});

		Map<Long, String> names = new LinkedHashMap<>();
		vcsProviders.forEach(p -> names.put(p.getId(), p.getI18NName()));

		return names;
	}

}
