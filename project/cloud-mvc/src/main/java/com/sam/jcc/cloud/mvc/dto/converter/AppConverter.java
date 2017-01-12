/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import org.springframework.core.convert.converter.Converter;

import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.mvc.dto.AppDTO;

/**
 * @author olegk
 *
 */
public class AppConverter implements Converter<IAppMetadata, AppDTO> {

	@Override
	public AppDTO convert(IAppMetadata source) {
		AppDTO appDTO = new AppDTO();
		appDTO.setId(source.getId());
		appDTO.setProjectName(source.getProjectName());
		return appDTO;
	}

}
