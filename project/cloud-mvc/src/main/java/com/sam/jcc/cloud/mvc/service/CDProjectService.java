/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.CDProjectDTO;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class CDProjectService extends BaseService<CDProjectDTO> {

	@Autowired
	private IService<IProjectMetadata> projectProviderService;

	public CDProjectDTO convertModel(IProjectMetadata model) {
		return conversionService.convert(model, CDProjectDTO.class);
	}

	public List<CDProjectDTO> convertModels(List<? super IProjectMetadata> models) {
		List<CDProjectDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, CDProjectDTO.class)));
		return dtos;
	}

	public List<? super CDProjectDTO> findAll() {
		List<CDProjectDTO> projects = new ArrayList<>();
		projects.addAll(convertModels(projectProviderService.findAll()));
		return projects;
	}

	public CDProjectDTO update(CDProjectDTO cdProjectDTO) {
		throw new UnsupportedCallException();
	}

}
