/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import org.springframework.core.convert.converter.Converter;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.CIProjectDTO;

/**
 * @author olegk
 *
 */
public class CIProjectConverter implements Converter<IProjectMetadata, CIProjectDTO> {

	@Override
	public CIProjectDTO convert(IProjectMetadata source) {
		CIProjectDTO ciProjectDTO = new CIProjectDTO();
		ciProjectDTO.setId(source.getId());
		ciProjectDTO.setName(source.getName());
		ciProjectDTO.setHasSources(source.hasSources());
		ciProjectDTO.setHasVcs(source.hasVCS());
		ciProjectDTO.setHasCI(source.hasCI());
		return ciProjectDTO;
	}

}
