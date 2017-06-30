/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.cq.ICQMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.CQProjectDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class CQProjectService extends BaseService<CQProjectDTO> {

	@Autowired
	private IService<IProjectMetadata> projectProviderService;

	@Autowired
	private IService<ICQMetadata> cqProviderService;

	@Autowired
	private ProjectService projectService;

	public CQProjectDTO convertModel(IProjectMetadata model) {
		return conversionService.convert(model, CQProjectDTO.class);
	}

	public List<CQProjectDTO> convertModels(List<? super IProjectMetadata> models) {
		List<CQProjectDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, CQProjectDTO.class)));
		return dtos;
	}

	public List<? super CQProjectDTO> findAll() {
		List<CQProjectDTO> projects = new ArrayList<>();
		projects.addAll(convertModels(projectProviderService.findAll()));
		return projects;
	}

	public CQProjectDTO update(CQProjectDTO dbProjectDTO) {
		Map<?, ?> props = conversionService.convert(dbProjectDTO, Map.class);
		cqProviderService.update(props);
		//
		IProjectMetadata read = projectService.findProjectById(dbProjectDTO.getId());
		return convertModel(read);
	}

}
