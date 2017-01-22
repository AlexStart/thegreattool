package com.sam.jcc.cloud.app;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.persistence.data.EntityNotFoundException;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
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
class AppMetadataDao implements ICRUD<AppMetadata> {

    @Autowired
    private ProjectDataRepository repository;

    @Override
    public AppMetadata create(AppMetadata m) {
        final ProjectData entity = convert(m);
        repository.save(entity);
        m.setId(entity.getId());
        return m;
    }

    @Override
    public AppMetadata read(AppMetadata m) {
        return convert(getOrThrow(m));
    }

    @Override
    public AppMetadata update(AppMetadata m) {
        getOrThrow(m);
        repository.save(convert(m));
        return m;
    }

    @Override
    public void delete(AppMetadata m) {
        repository.delete(getOrThrow(m));
    }

    private ProjectData getOrThrow(AppMetadata metadata) {
        final String name = metadata.getProjectName();
        final Optional<ProjectData> entity = repository.findByName(name);
        return entity.orElseThrow(
                () -> new EntityNotFoundException(metadata)
        );
    }

    @Override
    public List<IAppMetadata> findAll() {
        return newArrayList(repository.findAll())
                .stream()
                .map(this::convert)
                .collect(toList());
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
