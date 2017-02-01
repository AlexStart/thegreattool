package com.sam.jcc.cloud.dataprovider.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDBManager;
import com.sam.jcc.cloud.i.data.IDataInjector;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.data.ISourceGenerator;
import com.sam.jcc.cloud.i.data.ISqlDataProvider;

/**
 * @author Alec Kotovich
 * @author Alexey Zhytnik
 */
public abstract class SqlDataProvider<T extends IDataMetadata> extends AbstractDataProvider implements ISqlDataProvider {

    @Autowired
    public SqlDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    public IDataMetadata process(IDataMetadata m) {
    	final T app = (T) asAppData(m);
        getDataInjector().inject(app);
        getSourceGenerator().generate(app);
        getDbManager().create(app);
        return app;
    }


	protected abstract ISourceGenerator<T> getSourceGenerator();

	protected abstract IDataInjector<T> getDataInjector();
	
	protected abstract IDBManager<T> getDbManager();	
    
    
}
