/**
 * 
 */
package com.sam.jcc.cloud.dataprovider.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDataMetadata;

/**
 * @author olegk
 *
 */
@Component
public class MongoDataProvider extends NoSqlDataProvider {

	private static final long MONGO_PROVIDER_ID = 7L;

	public MongoDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public Long getId() {
		return MONGO_PROVIDER_ID;
	}

}
