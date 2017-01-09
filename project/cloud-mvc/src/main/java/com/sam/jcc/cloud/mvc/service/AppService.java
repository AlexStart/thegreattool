/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.mvc.dto.AppDTO;

/**
 * @author olegk
 *
 */
@Service
public class AppService {
	
	@Autowired
	private ConversionService conversionService;

	public Map<String, String> convertDTO(AppDTO form) {
		return conversionService.convert(form, Map.class);
	}

	public AppDTO convertModel(IAppMetadata model) {
		return conversionService.convert(model, AppDTO.class);
	}

	public List<? super AppDTO> convertModels(List<? super IAppMetadata> models) {
		List<AppDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, AppDTO.class)));
		return dtos;
	}
}
