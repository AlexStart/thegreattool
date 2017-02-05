/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.mvc.dto.DbDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class DbService extends BaseService<DbDTO> {

	@Autowired
	private IService<IDataMetadata> dbProviderService;

	public DbDTO convertModel(IDataMetadata model) {
		return conversionService.convert(model, DbDTO.class);
	}

	public List<? super DbDTO> convertModels(List<? super IDataMetadata> models) {
		List<DbDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, DbDTO.class)));
		return dtos;
	}

	public List<ProviderDTO> getDbProviders() {
		List<ProviderDTO> providerDTOs = new ArrayList<>();
		Map<Long, String> names = dbProviderService.getNames();
		for (Long id : names.keySet()) {
			ProviderDTO providerDTO = new ProviderDTO();
			providerDTO.setProviderId(id);
			providerDTO.setProviderName(names.get(id));
			providerDTOs.add(providerDTO);
		}
		return providerDTOs;
	}
}
