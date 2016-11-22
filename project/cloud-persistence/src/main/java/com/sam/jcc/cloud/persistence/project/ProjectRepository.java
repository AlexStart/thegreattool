package com.sam.jcc.cloud.persistence.project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Repository
@Transactional
public interface ProjectRepository extends CrudRepository<ProjectMetadataEntity, Long> {

    ProjectMetadataEntity findByGroupIdAndArtifactId(String groupId, String artifactId);
}
