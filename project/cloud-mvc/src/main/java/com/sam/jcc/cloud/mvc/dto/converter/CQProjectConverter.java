/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import org.springframework.core.convert.converter.Converter;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.CQProjectDTO;

/**
 * @author olegk
 *
 */
public class CQProjectConverter implements Converter<IProjectMetadata, CQProjectDTO> {

	@Override
	public CQProjectDTO convert(IProjectMetadata source) {
		CQProjectDTO cqProjectDTO = new CQProjectDTO();
		cqProjectDTO.setId(source.getId());
		cqProjectDTO.setName(source.getName());
		cqProjectDTO.setHasSources(source.hasSources());
		cqProjectDTO.setHasVcs(source.hasVCS());
		cqProjectDTO.setHasDb(source.hasDb());
		cqProjectDTO.setDb(source.getDb());
		cqProjectDTO.setHasCQ(source.hasCQ());
		cqProjectDTO.setCQ(source.getCQ());
		return cqProjectDTO;
	}

}
