/**
 * 
 */
package com.sam.jcc.cloud.mvc.dto.converter;

import org.springframework.core.convert.converter.Converter;

import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.mvc.dto.VCSDTO;

/**
 * @author olegk
 *
 */
public class VCSConverter implements Converter<IVCSMetadata, VCSDTO> {

	@Override
	public VCSDTO convert(IVCSMetadata source) {
		VCSDTO vcsDTO = new VCSDTO();
		vcsDTO.setName(source.getName());
		vcsDTO.setDescription(source.getDescription());
		return vcsDTO;
	}

}
