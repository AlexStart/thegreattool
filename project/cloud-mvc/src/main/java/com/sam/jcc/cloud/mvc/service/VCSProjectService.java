/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.VCSProjectDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class VCSProjectService extends BaseService<VCSProjectDTO> {

	@Autowired
	private List<IService<IProjectMetadata>> projectProviderServices;

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
		for (IService<IProjectMetadata> projectProviderService : projectProviderServices) {
			projects.addAll(convertModels(projectProviderService.findAll()));
		}
		return projects;
	}

}
