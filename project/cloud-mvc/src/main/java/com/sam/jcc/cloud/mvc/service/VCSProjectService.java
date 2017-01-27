/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.mvc.dto.VCSProjectDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class VCSProjectService extends BaseService<VCSProjectDTO> {

	@Autowired
	private IService<IProjectMetadata> projectProviderService;

	@Autowired
	private IService<IVCSMetadata> vcsProviderService;

	@Autowired
	private VCSProjectService vcsProjectService;

	@Autowired
	private ProjectService projectService;

	public VCSProjectDTO convertModel(IProjectMetadata model) {
		return conversionService.convert(model, VCSProjectDTO.class);
	}

	public List<VCSProjectDTO> convertModels(List<? super IProjectMetadata> models) {
		List<VCSProjectDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, VCSProjectDTO.class)));
		return dtos;
	}

	public List<? super VCSProjectDTO> findAll() {
		List<VCSProjectDTO> projects = new ArrayList<>();
		projects.addAll(convertModels(projectProviderService.findAll()));
		return projects;
	}

	public VCSProjectDTO update(VCSProjectDTO vcsProjectDTO) {
		Map<?, ?> props = conversionService.convert(vcsProjectDTO, Map.class);
		vcsProviderService.update(props);
		//
		IProjectMetadata read = projectService.findProjectById(vcsProjectDTO.getId());
		return vcsProjectService.convertModel(read);
	}

}
