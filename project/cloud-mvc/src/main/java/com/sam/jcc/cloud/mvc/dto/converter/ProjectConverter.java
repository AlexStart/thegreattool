/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import org.springframework.core.convert.converter.Converter;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.ProjectDTO;

/**
 * @author olegk
 *
 */
public class ProjectConverter implements Converter<IProjectMetadata, ProjectDTO> {

	@Override
	public ProjectDTO convert(IProjectMetadata source) {
		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setId(source.getId());
		projectDTO.setName(source.getName());
		projectDTO.setHasSources(source.hasSources());
		projectDTO.setHasVcs(source.hasVCS());
		projectDTO.setHasCI(source.hasCI());
		projectDTO.setHasDb(source.hasDb());
		return projectDTO;
	}

}
