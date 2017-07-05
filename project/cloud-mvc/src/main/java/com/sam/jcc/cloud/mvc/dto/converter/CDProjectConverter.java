/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import org.springframework.core.convert.converter.Converter;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.CDProjectDTO;

/**
 * @author olegk
 *
 */
public class CDProjectConverter implements Converter<IProjectMetadata, CDProjectDTO> {

	@Override
	public CDProjectDTO convert(IProjectMetadata source) {
		CDProjectDTO cdProjectDTO = new CDProjectDTO();
		cdProjectDTO.setId(source.getId());
		cdProjectDTO.setName(source.getName());
		cdProjectDTO.setHasSources(source.hasSources());
		cdProjectDTO.setHasVcs(source.hasVCS());
		cdProjectDTO.setHasCI(source.hasCI());
		cdProjectDTO.setCi(source.getCi());
		return cdProjectDTO;
	}

}
