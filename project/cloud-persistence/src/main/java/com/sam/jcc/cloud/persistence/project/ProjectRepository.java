package com.sam.jcc.cloud.persistence.project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Repository
public interface ProjectRepository extends CrudRepository<ProjectMetadataEntity, Long> {

    ProjectMetadataEntity findByGroupIdAndArtifactId(String groupId, String artifactId);
}
