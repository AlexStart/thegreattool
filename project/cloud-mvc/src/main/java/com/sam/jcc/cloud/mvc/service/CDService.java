/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.cd.ICDMetadata;
import com.sam.jcc.cloud.mvc.dto.CDDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class CDService extends BaseService<CDDTO> {

	@Autowired
	private IService<ICDMetadata> cdProviderService;

	public CDDTO convertModel(ICDMetadata model) {
		return conversionService.convert(model, CDDTO.class);
	}

	public List<? super CDDTO> convertModels(List<? super ICDMetadata> models) {
		List<CDDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, CDDTO.class)));
		return dtos;
	}

	public List<ProviderDTO> getCDProviders() {
		List<ProviderDTO> providerDTOs = new ArrayList<>();
		Map<Long, String> names = cdProviderService.getNames();
		for (Long id : names.keySet()) {
			ProviderDTO providerDTO = new ProviderDTO();
			providerDTO.setProviderId(id);
			providerDTO.setProviderName(names.get(id));
			providerDTOs.add(providerDTO);
		}
		return providerDTOs;
	}

}
