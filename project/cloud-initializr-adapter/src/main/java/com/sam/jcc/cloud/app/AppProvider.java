package com.sam.jcc.cloud.app;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.i.app.IAppProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Component
public class AppProvider extends AbstractProvider<IAppMetadata> implements IAppProvider {

    @Autowired
    private AppMetadataDao dao;

    @Autowired
    private AppMetadataValidator validator;

    @Autowired
    public AppProvider(List<IEventManager<IAppMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    public boolean supports(IAppMetadata m) {
        return m instanceof AppMetadata;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IAppMetadata preprocess(IAppMetadata m) {
        if (isNull(m.getProjectName())) throw new AppMetadataValidationException();

        final String name = m.getProjectName().trim().toLowerCase();
        m.setProjectName(name);
        validator.validate(m);
        return m;
    }

    @Override
    public IAppMetadata process(IAppMetadata m) {
        final IAppMetadata created = dao.create(m);
        updateStatus(m, AppMetadataStatus.CREATED);
        return created;
    }

    @Override
    public IAppMetadata postprocess(IAppMetadata m) {
        return asAppMetadata(m);
    }

    @Override
    public IAppMetadata read(IAppMetadata m) {
        return dao.read(asAppMetadata(m));
    }

    @Override
    public IAppMetadata update(IAppMetadata m) {
        final IAppMetadata updated = dao.update(asAppMetadata(m));
        updateStatus(m, AppMetadataStatus.UPDATED);
        return updated;
    }

    @Override
    public void delete(IAppMetadata m) {
        dao.delete(asAppMetadata(m));
    }

    @Override
    public List<? super IAppMetadata> findAll() {
        return dao.findAll();
    }

    private void updateStatus(IAppMetadata app, AppMetadataStatus status) {
        asAppMetadata(app).setStatus(status);
        eventManagers.forEach(manager -> manager.fireEvent(app, this));
    }

    private AppMetadata asAppMetadata(IAppMetadata metadata) {
        return (AppMetadata) metadata;
    }
}
