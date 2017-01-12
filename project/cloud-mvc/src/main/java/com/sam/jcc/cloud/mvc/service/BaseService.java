/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import com.sam.jcc.cloud.mvc.dto.BaseDTO;

/**
 * @author olegk
 *
 */
public class BaseService<T extends BaseDTO> {

	@Autowired
	protected ConversionService conversionService;

	@SuppressWarnings("unchecked")
	public Map<String, String> convertDTO(T form) {
		return conversionService.convert(form, Map.class);
	}


	
}
