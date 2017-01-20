/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import org.springframework.core.convert.converter.Converter;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.VCSProjectDTO;

/**
 * @author olegk
 *
 */
public class VCSProjectConverter implements Converter<IProjectMetadata, VCSProjectDTO> {

	@Override
	public VCSProjectDTO convert(IProjectMetadata source) {
		VCSProjectDTO vcsProjectDTO = new VCSProjectDTO();
		vcsProjectDTO.setId(source.getId());
		vcsProjectDTO.setName(source.getName());
		vcsProjectDTO.setHasSources(source.hasSources());
		return vcsProjectDTO;
	}

}
