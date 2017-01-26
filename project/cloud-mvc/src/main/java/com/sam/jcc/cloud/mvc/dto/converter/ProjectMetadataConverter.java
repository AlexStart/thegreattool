/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.jcc.cloud.mvc.dto.ProjectDTO;

/**
 * @author olegk
 *
 */
public class ProjectMetadataConverter implements Converter<ProjectDTO, Map<?, ?>> {

	private static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

	@Override
	public Map<?, ?> convert(ProjectDTO source) {
		return OBJ_MAPPER.convertValue(source, Map.class);
	}

}
