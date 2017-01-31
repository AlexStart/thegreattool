/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.mvc.dto.CIDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class CIService extends BaseService<CIDTO> {

	@Autowired
	private IService<ICIMetadata> ciProviderService;

	public CIDTO convertModel(ICIMetadata model) {
		return conversionService.convert(model, CIDTO.class);
	}

	public List<? super CIDTO> convertModels(List<? super ICIMetadata> models) {
		List<CIDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, CIDTO.class)));
		return dtos;
	}

	public List<ProviderDTO> getCIProviders() {
		List<ProviderDTO> providerDTOs = new ArrayList<>();
		Map<Long, String> names = ciProviderService.getNames();
		for (Long id : names.keySet()) {
			ProviderDTO providerDTO = new ProviderDTO();
			providerDTO.setProviderId(id);
			providerDTO.setProviderName(names.get(id));
			providerDTOs.add(providerDTO);
		}
		return providerDTOs;
	}
}
