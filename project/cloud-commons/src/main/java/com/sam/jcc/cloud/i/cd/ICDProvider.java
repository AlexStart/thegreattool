package com.sam.jcc.cloud.i.cd;

import com.sam.jcc.cloud.i.IProvider;

/**
 * @author olegk
 * @since 4-Jul-2017
 */
public interface ICDProvider extends IProvider<ICDMetadata> {

	ICDMetadata create(ICDMetadata ciMetadata);
	
	String getType();
}
