package com.sam.jcc.cloud.app;

import static java.util.Objects.isNull;

import java.util.List;

import com.sam.jcc.cloud.provider.UnsupportedCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.i.app.IAppProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.utils.project.ArtifactIdValidator;
import com.sam.jcc.cloud.utils.project.ArtifactIdValidator.AppMetadataValidationException;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Component
public class AppProvider extends AbstractProvider<IAppMetadata> implements IAppProvider {

    @Autowired
    private AppMetadataDao dao;

    @Autowired
    private ArtifactIdValidator nameValidator;

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
        nameValidator.validate(name);
        m.setProjectName(name);
        return m;
    }

    @Override
    public IAppMetadata process(IAppMetadata m) {
        return dao.create(asAppMetadata(m));
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
        throw new UnsupportedCallException();
    }

    @Override
    public void delete(IAppMetadata m) {
        dao.delete(asAppMetadata(m));
    }

    @Override
    public List<IAppMetadata> findAll() {
        return dao.findAll();
    }

    private AppMetadata asAppMetadata(IAppMetadata metadata) {
        return (AppMetadata) metadata;
    }
}
