/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.mvc.dto.ProjectDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.mvc.dto.VCSDTO;
import com.sam.jcc.cloud.mvc.dto.VCSProjectDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class VCSService extends BaseService<VCSDTO> {

	@Autowired
	private IService<IVCSMetadata> vcsProviderService;

	public VCSDTO convertModel(IVCSMetadata model) {
		return conversionService.convert(model, VCSDTO.class);
	}

	public List<? super VCSDTO> convertModels(List<? super IVCSMetadata> models) {
		List<VCSDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, VCSDTO.class)));
		return dtos;
	}

	public List<ProviderDTO> getVCSProviders() {
		List<ProviderDTO> providerDTOs = new ArrayList<>();
		Map<Long, String> names = vcsProviderService.getNames();
		for (Long id : names.keySet()) {
			ProviderDTO providerDTO = new ProviderDTO();
			providerDTO.setProviderId(id);
			providerDTO.setProviderName(names.get(id));
			providerDTOs.add(providerDTO);
		}
		return providerDTOs;
	}

	public ProjectDTO update(VCSProjectDTO vcsProjectDTO) {
		// TODO Auto-generated method stub
		return null;
	}
}
