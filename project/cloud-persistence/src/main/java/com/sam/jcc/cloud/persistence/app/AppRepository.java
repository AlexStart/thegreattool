package com.sam.jcc.cloud.persistence.app;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Repository
public interface AppRepository extends CrudRepository<AppMetadataEntity, Long> {

    Optional<AppMetadataEntity> findByProjectName(String projectName);
}
