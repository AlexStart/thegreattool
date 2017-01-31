package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.data.ISqlDataProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Alec Kotovich
 * @author Alexey Zhytnik
 */
public abstract class SqlDataProvider extends AbstractDataProvider implements ISqlDataProvider {

    @Autowired
    private MySqlInjector injector;

    @Autowired
    private MySqlDatabaseManager dbManager;

    @Autowired
    private JpaSourceGenerator sourceGenerator;

    @Autowired
    public SqlDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    public IDataMetadata process(IDataMetadata d) {
        final AppData app = asAppData(d);
        injector.inject(app);
        sourceGenerator.generate(app);
        dbManager.create(app);
        return app;
    }
}
