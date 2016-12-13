/**
 * 
 */
package com.sam.jcc.cloud.dataprovider.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.dataprovider.NoSqlDataProvider;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDataMetadata;

/**
 * @author olegk
 *
 */
@Component
public class MongoDataProvider extends NoSqlDataProvider {

	public MongoDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
		super(eventManagers);
	}

}
