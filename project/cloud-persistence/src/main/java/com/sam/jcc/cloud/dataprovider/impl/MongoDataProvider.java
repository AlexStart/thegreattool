/**
 * 
 */
package com.sam.jcc.cloud.dataprovider.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDBManager;
import com.sam.jcc.cloud.i.data.IDataInjector;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.data.ISourceGenerator;

/**
 * @author olegk
 *
 */
@Component
public class MongoDataProvider extends NoSqlDataProvider<AppData> {

	@Autowired
	private MongoDbInjector injector;

	@Autowired
	private MongoDatabaseManager dbManager;

	@Autowired
	private MongoSourceGenerator sourceGenerator;

	private static final long MONGO_PROVIDER_ID = 7L;

	public MongoDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public Long getId() {
		return MONGO_PROVIDER_ID;
	}

	@Override
	protected ISourceGenerator<AppData> getSourceGenerator() {
		return sourceGenerator;
	}

	@Override
	protected IDataInjector<AppData> getDataInjector() {
		return injector;
	}

	@Override
	protected IDBManager<AppData> getDbManager() {
		return dbManager;
	}

}
