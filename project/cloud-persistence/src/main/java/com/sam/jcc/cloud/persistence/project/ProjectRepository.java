package com.sam.jcc.cloud.persistence.project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Repository
public interface ProjectRepository extends CrudRepository<ProjectMetadataEntity, Long> {

    Iterable<ProjectMetadataEntity> findByProjectType(String projectType);

    Optional<ProjectMetadataEntity> findByGroupIdAndArtifactId(String groupId, String artifactId);
}
