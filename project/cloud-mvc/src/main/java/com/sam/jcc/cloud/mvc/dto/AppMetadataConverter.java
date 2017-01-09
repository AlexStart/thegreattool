/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author olegk
 *
 */
public class AppMetadataConverter implements Converter<AppDTO, Map<String, String>> {

	private static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

	@Override
	public Map<String, String> convert(AppDTO source) {
		return OBJ_MAPPER.convertValue(source, Map.class);
	}

}
