package com.sam.jcc.cloud.app;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.persistence.app.AppMetadataEntity;
import com.sam.jcc.cloud.persistence.app.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Component
//TODO: maybe check existence
class AppMetadataDao implements ICRUD<IAppMetadata> {

    @Autowired
    private AppRepository repository;

    @Override
    public IAppMetadata create(IAppMetadata m) {
        repository.save(convert(asAppMetadata(m)));
        return m;
    }

    @Override
    public IAppMetadata read(IAppMetadata m) {
        final String name = asAppMetadata(m).getProjectName();
        final Optional<AppMetadataEntity> entity = repository.findByProjectName(name);
        return convert(entity.orElseThrow(InternalCloudException::new));
    }

    @Override
    public IAppMetadata update(IAppMetadata m) {
        repository.save(convert(asAppMetadata(m)));
        return m;
    }

    @Override
    public void delete(IAppMetadata m) {
        final Long id = asAppMetadata(m).getId();
        repository.delete(id);
    }

    @Override
    public List<? super IAppMetadata> findAll() {
        return newArrayList(repository.findAll())
                .stream()
                .map(this::convert)
                .collect(toList());
    }

    private AppMetadata asAppMetadata(IAppMetadata metadata) {
        return (AppMetadata) metadata;
    }

    private AppMetadata convert(AppMetadataEntity entity) {
        final AppMetadata app = new AppMetadata();
        app.setId(entity.getId());
        app.setProjectName(entity.getProjectName());
        return app;
    }

    private AppMetadataEntity convert(AppMetadata app) {
        final AppMetadataEntity entity = new AppMetadataEntity();
        entity.setId(app.getId());
        entity.setProjectName(app.getProjectName());
        return entity;
    }
}
