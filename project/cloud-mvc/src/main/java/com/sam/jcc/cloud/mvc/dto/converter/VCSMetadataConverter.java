/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.jcc.cloud.mvc.dto.VCSDTO;

/**
 * @author olegk
 *
 */
public class VCSMetadataConverter implements Converter<VCSDTO, Map<String, String>> {

	private static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

	@Override
	public Map<String, String> convert(VCSDTO source) {
		return OBJ_MAPPER.convertValue(source, Map.class);
	}

}
