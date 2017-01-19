/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.mvc.dto.HealthDTO;

/**
 * @author olegk
 *
 */
public class HealthConverter implements Converter<IHealthMetadata, HealthDTO> {

	@Override
	public HealthDTO convert(IHealthMetadata source) {
		HealthDTO healthDTO = new HealthDTO();
		BeanUtils.copyProperties(source, healthDTO);
		healthDTO.setUrl(healthDTO.getUrl().replaceAll("\n", "<br/>"));
		return healthDTO;
	}

}
