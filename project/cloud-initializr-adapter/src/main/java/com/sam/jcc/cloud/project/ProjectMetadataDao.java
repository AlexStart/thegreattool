package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 23.11.2016
 */
@Component
public class ProjectMetadataDao implements ICRUD<IProjectMetadata> {

    @Autowired
    private ProjectDataRepository repository;

    @Override
    public IProjectMetadata create(IProjectMetadata m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProjectMetadata read(IProjectMetadata m) {
        return convert(getOrThrow(m));
    }

    @Override
    public IProjectMetadata update(IProjectMetadata m) {
        getOrThrow(m);
        repository.save(convert(asMetadata(m)));
        return m;
    }

    @Override
    public void delete(IProjectMetadata m) {
        repository.delete(getOrThrow(m));
    }

    @Override
    public List<IProjectMetadata> findAll() {
        return repository.findBySourcesNotNull()
                .stream()
                .map(this::convert)
                .collect(toList());
    }

    private ProjectData getOrThrow(IProjectMetadata metadata) {
        final String name = asMetadata(metadata).getProjectName();
        final Optional<ProjectData> entity = repository.findByName(name);
        return entity.orElseThrow(
                () -> new ProjectNotFoundException(metadata)
        );
    }

    private ProjectMetadata asMetadata(IProjectMetadata metadata) {
        return (ProjectMetadata) metadata;
    }

    private ProjectMetadata convert(ProjectData data) {
        final ProjectMetadata metadata = new ProjectMetadata();

        metadata.setId(data.getId());
        metadata.setProjectName(data.getName());
        metadata.setProjectSources(data.getSources());
        return metadata;
    }

    private ProjectData convert(ProjectMetadata metadata) {
        final ProjectData data = new ProjectData();

        data.setId(metadata.getId());
        data.setSources(metadata.getProjectSources());
        return data;
    }
}
