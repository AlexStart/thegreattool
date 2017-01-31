/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.CIProjectDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class CIProjectService extends BaseService<CIProjectDTO> {

	@Autowired
	private IService<IProjectMetadata> projectProviderService;

	@Autowired
	private IService<ICIMetadata> ciProviderService;

	@Autowired
	private CIProjectService ciProjectService;

	@Autowired
	private ProjectService projectService;

	public CIProjectDTO convertModel(IProjectMetadata model) {
		return conversionService.convert(model, CIProjectDTO.class);
	}

	public List<CIProjectDTO> convertModels(List<? super IProjectMetadata> models) {
		List<CIProjectDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, CIProjectDTO.class)));
		return dtos;
	}

	public List<? super CIProjectDTO> findAll() {
		List<CIProjectDTO> projects = new ArrayList<>();
		projects.addAll(convertModels(projectProviderService.findAll()));
		return projects;
	}

	public CIProjectDTO update(CIProjectDTO ciProjectDTO) {
		Map<?, ?> props = conversionService.convert(ciProjectDTO, Map.class);
		ciProviderService.update(props);
		//
		IProjectMetadata read = projectService.findProjectById(ciProjectDTO.getId());
		return ciProjectService.convertModel(read);
	}

}
