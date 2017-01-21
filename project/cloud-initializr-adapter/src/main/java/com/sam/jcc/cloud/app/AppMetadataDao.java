package com.sam.jcc.cloud.app;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import com.sam.jcc.cloud.persistence.exception.EntityNotFoundException;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
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
class AppMetadataDao implements ICRUD<IAppMetadata> {

    @Autowired
    private ProjectDataRepository repository;

    @Override
    public IAppMetadata create(IAppMetadata m) {
        final ProjectData entity = convert(asAppMetadata(m));
        repository.save(entity);
        m.setId(entity.getId());
        return m;
    }

    @Override
    public IAppMetadata read(IAppMetadata m) {
        final String name = asAppMetadata(m).getProjectName();
        final Optional<ProjectData> entity = repository.findByName(name);
        return convert(entity.orElseThrow(() -> new EntityNotFoundException(m)));
    }

    @Override
    public IAppMetadata update(IAppMetadata m) {
        throw new UnsupportedCallException();
    }

    @Override
    public void delete(IAppMetadata m) {
        final ProjectData entity = convert(asAppMetadata(m));
        repository.delete(entity);
    }

    @Override
    public List<IAppMetadata> findAll() {
        return newArrayList(repository.findAll())
                .stream()
                .map(this::convert)
                .collect(toList());
    }

    private AppMetadata asAppMetadata(IAppMetadata metadata) {
        return (AppMetadata) metadata;
    }

    private AppMetadata convert(ProjectData entity) {
        final AppMetadata app = new AppMetadata();
        app.setId(entity.getId());
        app.setProjectName(entity.getName());
        app.setSources(entity.getSources());
        return app;
    }

    private ProjectData convert(AppMetadata app) {
        final ProjectData data = new ProjectData();
        data.setId(app.getId());
        data.setName(app.getProjectName());
        return data;
    }
}
