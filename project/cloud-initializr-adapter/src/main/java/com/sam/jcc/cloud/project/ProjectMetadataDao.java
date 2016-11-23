package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.ICRUD;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.persistence.project.ProjectMetadataEntity;
import com.sam.jcc.cloud.persistence.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 23.11.2016
 */
@Component
class ProjectMetadataDao implements ICRUD<ProjectMetadata> {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectEntityConverter entityConverter;
    @Autowired
    private ProjectMetadataConverter metadataConverter;

    @Override
    public ProjectMetadata create(ProjectMetadata metadata) {
        final ProjectMetadataEntity entity = metadataConverter.convert(metadata);
        repository.save(entity);
        metadata.setId(entity.getId());
        return metadata;
    }

    @Override
    public ProjectMetadata read(ProjectMetadata metadata) {
        final ProjectMetadataEntity entity = search(metadata);
        return entityConverter.convert(entity);
    }

    @Override
    public ProjectMetadata update(ProjectMetadata metadata) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ProjectMetadata metadata) {
        final ProjectMetadataEntity entity = search(metadata);
        repository.delete(entity);
    }

    @Override
    @Transactional
    public List<IProjectMetadata> findAll() {
        return newArrayList(repository.findAll())
                .stream()
                .map(entityConverter::convert)
                .collect(toList());
    }

    private ProjectMetadataEntity search(ProjectMetadata metadata) {
        return repository.findByGroupIdAndArtifactId(
                metadata.getGroupId(),
                metadata.getArtifactId()
        );
    }
}
