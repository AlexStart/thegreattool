package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
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
        byte[] sources = zipManager.zip(metadata.getDirectory());
        metadata.setProjectSources(sources);
    }

    public void reset(ProjectMetadata metadata) {
        clearTempFolder(metadata);
    }

    private void clearTempFolder(ProjectMetadata metadata) {
        fileManager.delete(metadata.getDirectory());
    }
}
