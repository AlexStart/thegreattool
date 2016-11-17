package com.sam.jcc.cloud.project;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static com.sam.jcc.cloud.project.ZipUtil.archivateDir;
import static org.apache.commons.io.FileUtils.cleanDirectory;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Component
class ProjectBuilder {

    @Autowired
    private ProjectGenerator generator;
    @Autowired
    private ProjectMetadataConverter converter;

    public byte[] build(ProjectMetadata project) {
        final ProjectRequest request = converter.convert(project);
        final File dir = generator.generateProjectStructure(request);
        try {
            return archivateDir(dir);
        } finally {
            cleanUp(dir);
        }
    }

    private void cleanUp(File dir) {
        try {
            cleanDirectory(dir);

            if (!dir.delete()) {
                throw new RuntimeException("Can't delete " + dir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
