/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.cq.ICQMetadata;
import com.sam.jcc.cloud.mvc.dto.CQDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class CQService extends BaseService<CQDTO> {

	@Autowired
	private IService<ICQMetadata> cqProviderService;

	public CQDTO convertModel(ICQMetadata model) {
		return conversionService.convert(model, CQDTO.class);
	}

	public List<? super CQDTO> convertModels(List<? super ICQMetadata> models) {
		List<CQDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, CQDTO.class)));
		return dtos;
	}

	public List<ProviderDTO> getCQProviders() {
		List<ProviderDTO> providerDTOs = new ArrayList<>();
		Map<Long, String> names = cqProviderService.getNames();
		for (Long id : names.keySet()) {
			ProviderDTO providerDTO = new ProviderDTO();
			providerDTO.setProviderId(id);
			providerDTO.setProviderName(names.get(id));
			providerDTOs.add(providerDTO);
		}
		return providerDTOs;
	}
}
