package com.sam.jcc.cloud.persistence.data;

import com.sam.jcc.cloud.i.Experimental;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Repository
@Experimental("Repository for all projects of the app")
public interface ProjectDataRepository extends CrudRepository<ProjectData, Long> {

    Optional<ProjectData> findByName(String name);
}
