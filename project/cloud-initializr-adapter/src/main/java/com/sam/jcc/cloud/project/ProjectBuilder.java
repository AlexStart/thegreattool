package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.InternalCloudException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.sam.jcc.cloud.project.ZipUtil.zip;

/**
 * @author Alexey Zhytnik
 * @since 23.11.2016
 */
@Component
class ProjectBuilder {

    @Autowired
    private Cleaner cleaner;

    public void build(ProjectMetadata metadata) {
        try {
            archivate(metadata);
        } finally {
            clearTempFolder(metadata);
        }
    }

    private void archivate(ProjectMetadata metadata) {
        try {
            byte[] sources = zip(metadata.getDirectory());
            metadata.setProjectSources(sources);
        } catch (Exception e) {
            throw new InternalCloudException(e);
        }
    }

    public void reset(ProjectMetadata metadata) {
        clearTempFolder(metadata);
    }

    private void clearTempFolder(ProjectMetadata metadata) {
        cleaner.remove(metadata.getDirectory());
    }
}
