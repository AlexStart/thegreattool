package com.sam.jcc.cloud.i.ci;

import com.sam.jcc.cloud.i.IProvider;

/**
 * @author Alexey Zhytnik
 * @since 22-Dec-16
 */
public interface ICIProvider extends IProvider<ICIMetadata> {

	ICIMetadata create(ICIMetadata ciMetadata);
}
