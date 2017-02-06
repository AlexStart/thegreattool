package com.sam.jcc.cloud.i.data;

import com.sam.jcc.cloud.i.IProvider;

public interface IDataProvider extends IProvider<IDataMetadata> {

	IDataMetadata update(IDataMetadata metadata);
    
}
