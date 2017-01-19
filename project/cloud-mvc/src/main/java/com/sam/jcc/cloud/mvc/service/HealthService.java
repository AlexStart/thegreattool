/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.mvc.dto.HealthDTO;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class HealthService extends BaseService<HealthDTO> {
	
	@Autowired
	private IService<IHealthMetadata> serviceDelegate;
	
	private List<? super HealthDTO> convertModels(List<? super IHealthMetadata> models) {
		List<HealthDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, HealthDTO.class)));
		Collections.sort(dtos, (d1, d2) -> d1.getId().compareTo(d2.getId()));
		return dtos;
	}
	
	public List<? super HealthDTO> findAll() {
		List<? super IHealthMetadata> findAll = serviceDelegate.findAll();
		return convertModels(findAll);
	}
}
