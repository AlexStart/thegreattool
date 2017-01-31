package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.data.INoSqlDataProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Alec Kotovich
 * @author Alexey Zhytnik
 */
public abstract class NoSqlDataProvider extends AbstractDataProvider implements INoSqlDataProvider {

    @Autowired
    private MongoDbInjector injector;

    @Autowired
    private MongoDatabaseManager dbManager;

    @Autowired
    private NoSqlSourceGenerator sourceGenerator;

    public NoSqlDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    public IDataMetadata process(IDataMetadata m) {
        final AppData app = asAppData(m);

        injector.inject(app);
        sourceGenerator.generate(app);
        dbManager.create(app);
        return app;
    }
}
