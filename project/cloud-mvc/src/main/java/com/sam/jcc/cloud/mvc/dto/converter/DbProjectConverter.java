/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import org.springframework.core.convert.converter.Converter;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.DbProjectDTO;

/**
 * @author olegk
 *
 */
public class DbProjectConverter implements Converter<IProjectMetadata, DbProjectDTO> {

	@Override
	public DbProjectDTO convert(IProjectMetadata source) {
		DbProjectDTO dbProjectDTO = new DbProjectDTO();
		dbProjectDTO.setId(source.getId());
		dbProjectDTO.setName(source.getName());
		dbProjectDTO.setHasSources(source.hasSources());
		dbProjectDTO.setHasVcs(source.hasVCS());
		dbProjectDTO.setHasDb(source.hasDb());
		dbProjectDTO.setDb(source.getDb());
		return dbProjectDTO;
	}

}
