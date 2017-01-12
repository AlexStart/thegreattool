/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.mvc.dto.AppDTO;

/**
 * @author olegk
 *
 */
@Service
public class AppService extends BaseService<AppDTO> {
	
	public AppDTO convertModel(IAppMetadata model) {
		return conversionService.convert(model, AppDTO.class);
	}
	
	public List<? super AppDTO> convertModels(List<? super IAppMetadata> models) {
		List<AppDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, AppDTO.class)));
		return dtos;
	}
	
}
