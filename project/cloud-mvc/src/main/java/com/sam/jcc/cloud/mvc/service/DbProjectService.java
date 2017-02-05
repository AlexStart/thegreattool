/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.DbProjectDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class DbProjectService extends BaseService<DbProjectDTO> {

	@Autowired
	private IService<IProjectMetadata> projectProviderService;

	@Autowired
	private IService<IDataMetadata> dbProviderService;

	@Autowired
	private ProjectService projectService;

	public DbProjectDTO convertModel(IProjectMetadata model) {
		return conversionService.convert(model, DbProjectDTO.class);
	}

	public List<DbProjectDTO> convertModels(List<? super IProjectMetadata> models) {
		List<DbProjectDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, DbProjectDTO.class)));
		return dtos;
	}

	public List<? super DbProjectDTO> findAll() {
		List<DbProjectDTO> projects = new ArrayList<>();
		projects.addAll(convertModels(projectProviderService.findAll()));
		return projects;
	}

	public DbProjectDTO update(DbProjectDTO dbProjectDTO) {
		Map<?, ?> props = conversionService.convert(dbProjectDTO, Map.class);
		dbProviderService.update(props);
		//
		IProjectMetadata read = projectService.findProjectById(dbProjectDTO.getId());
		return convertModel(read);
	}

}
