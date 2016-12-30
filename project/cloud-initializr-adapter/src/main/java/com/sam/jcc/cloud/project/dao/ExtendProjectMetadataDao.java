package com.sam.jcc.cloud.project.dao;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.project.ProjectMetadata;

import java.util.List;

/**
 * @author Alexey Zhytnik
 * @since 30-Dec-16
 */
public interface ExtendProjectMetadataDao extends ICRUD<ProjectMetadata> {

    List<? extends IProjectMetadata> findAllByProjectType(String projectType);
}