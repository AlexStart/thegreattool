/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.ProjectDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class ProjectService extends BaseService<ProjectDTO> {

	@Autowired
	private List<IService<IProjectMetadata>> projectProviderServices;

	public ProjectDTO convertModel(IProjectMetadata model) {
		return conversionService.convert(model, ProjectDTO.class);
	}

	public List<ProjectDTO> convertModels(List<? super IProjectMetadata> models) {
		List<ProjectDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, ProjectDTO.class)));
		return dtos;
	}

	public List<? super ProjectDTO> findAll() {
		List<ProjectDTO> projects = new ArrayList<>();
		for (IService<IProjectMetadata> projectProviderService : projectProviderServices) {
			projects.addAll(convertModels(projectProviderService.findAll()));
		}
		return projects;
	}

}
