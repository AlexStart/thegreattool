package com.sam.jcc.cloud.dataprovider.impl;

import java.util.List;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDBManager;
import com.sam.jcc.cloud.i.data.IDataInjector;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.data.INoSqlDataProvider;
import com.sam.jcc.cloud.i.data.ISourceGenerator;

/**
 * @author Alec Kotovich
 * @author Alexey Zhytnik
 */
public abstract class NoSqlDataProvider<T extends IDataMetadata> extends AbstractDataProvider implements INoSqlDataProvider {

	public NoSqlDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public IDataMetadata process(IDataMetadata m) {
		final T app = (T) asAppData(m);
		getDataInjector().inject(app);
		getSourceGenerator().generate((T)app);
		getDbManager().create(app);
		return app;
	}

	protected abstract ISourceGenerator<T> getSourceGenerator();

	protected abstract IDataInjector<T> getDataInjector();
	
	protected abstract IDBManager<T> getDbManager();
	
	
}
