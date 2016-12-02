package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.InternalCloudException;
import com.sam.jcc.cloud.tool.FileManager;
import com.sam.jcc.cloud.tool.ZipArchiveManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Alexey Zhytnik
 * @since 23.11.2016
 */
@Component
class ProjectBuilder {

    @Autowired
    private FileManager fileManager;
    @Autowired
    private ZipArchiveManager zipManager;

    public void build(ProjectMetadata metadata) {
        try {
            archivate(metadata);
        } finally {
            clearTempFolder(metadata);
        }
    }

    private void archivate(ProjectMetadata metadata) {
        try {
            byte[] sources = zipManager.zip(metadata.getDirectory());
            metadata.setProjectSources(sources);
        } catch (Exception e) {
            throw new InternalCloudException(e);
        }
    }

    public void reset(ProjectMetadata metadata) {
        clearTempFolder(metadata);
    }

    private void clearTempFolder(ProjectMetadata metadata) {
        fileManager.delete(metadata.getDirectory());
    }
}
