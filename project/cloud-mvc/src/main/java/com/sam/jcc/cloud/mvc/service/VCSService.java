/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.mvc.dto.VCSDTO;

/**
 * @author olegk
 *
 */
@Service
public class VCSService extends BaseService<VCSDTO> {
	
	public VCSDTO convertModel(IVCSMetadata model) {
		return conversionService.convert(model, VCSDTO.class);
	}

	public List<? super VCSDTO> convertModels(List<? super IVCSMetadata> models) {
		List<VCSDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, VCSDTO.class)));
		return dtos;
	}
}
