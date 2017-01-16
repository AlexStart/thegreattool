package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.dataprovider.AppDataStatus;
import com.sam.jcc.cloud.dataprovider.ProjectSourcesNotFound;
import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.exception.NotSupportedOperationException;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.data.ISqlDataProvider;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import com.sam.jcc.cloud.persistence.exception.EntityNotFoundException;
import com.sam.jcc.cloud.provider.AbstractProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.sam.jcc.cloud.dataprovider.AppDataStatus.UPDATED;
import static java.util.Objects.isNull;

/**
 * @author Alec Kotovich
 * @author Alexey Zhytnik
 */
public abstract class SqlDataProvider extends AbstractProvider<IDataMetadata> implements ISqlDataProvider {

    @Autowired
    private ProjectDataRepository repository;

    @Autowired
    private MySqlDependencyInjector injector;

    @Autowired
    private TableNameValidator validator;

    @Autowired
    public SqlDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    public boolean supports(IDataMetadata d) {
        return d instanceof AppData;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IDataMetadata read(IDataMetadata d) {
        throw new NotSupportedOperationException();
    }

    @Override
    public IDataMetadata create(IDataMetadata d) {
        throw new NotSupportedOperationException();
    }

    @Override
    public void delete(IDataMetadata d) {
        throw new NotSupportedOperationException();
    }

    @Override
    public List<? super IDataMetadata> findAll() {
        throw new NotSupportedOperationException();
    }

    @Override
    public IDataMetadata update(IDataMetadata d) {
        super.create(d);
        updateStatus(d, UPDATED);
        return d;
    }

    @Override
    public IDataMetadata preprocess(IDataMetadata d) {
        final AppData app = asAppData(d);

        final ProjectData data = repository.findByName(app.getAppName())
                .orElseThrow(() -> new EntityNotFoundException(app));

        if (data.getDataSupport()) throw new InternalCloudException();
        if (isNull(data.getSources())) throw new ProjectSourcesNotFound(app);

        validator.validate(app.getAppName());

        app.setSources(data.getSources());
        return app;
    }

    @Override
    public IDataMetadata process(IDataMetadata d) {
        injector.inject(asAppData(d));
        return d;
    }

    @Override
    public IDataMetadata postprocess(IDataMetadata d) {
        return d;
    }

    private void updateStatus(IDataMetadata data, AppDataStatus status) {
        asAppData(data).setStatus(status);
        notify(data);
    }

    private AppData asAppData(IDataMetadata data) {
        return (AppData) data;
    }
}
