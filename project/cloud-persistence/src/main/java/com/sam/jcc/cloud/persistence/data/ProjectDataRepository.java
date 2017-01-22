package com.sam.jcc.cloud.persistence.data;

import com.sam.jcc.cloud.i.Experimental;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Repository
@Experimental("Repository for all projects of the app")
public interface ProjectDataRepository extends CrudRepository<ProjectData, Long> {

    List<ProjectData> findByCiNotNull();

    List<ProjectData> findByVcsNotNull();

    List<ProjectData> findBySourcesNotNull();

    Optional<ProjectData> findByCi(String ci);

    Optional<ProjectData> findByVcs(String vcs);

    Optional<ProjectData> findByName(String name);
}
