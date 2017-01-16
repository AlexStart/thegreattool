package com.sam.jcc.cloud.utils.project;

import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.utils.parsers.ProjectParser;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Alexey Zhytnik
 * @since 13.01.2017
 */
@Component
public class DependencyManager implements IDependencyManager<DependencyManager.Dependency> {

    @Setter
    @Autowired
    private FileManager files;

    private ProjectParser parser = new ProjectParser();

    private IDependencyManager<Dependency> mavenManager = new MavenDependencyManager();
    private IDependencyManager<Dependency> gradleManager = new GradleDependencyManager();

    @Override
    public String add(File zip, Dependency dependency) {
        final IDependencyManager<Dependency> manager = getManager(zip);

        try (TempFile config = files.createTempFile(zip.getName(), ".tmp")) {
            copyConfig(zip, config);

            final List<Dependency> dependencies = manager.getAllDependencies(config);
            if (dependencies.contains(dependency)) throw new InternalCloudException();

            return manager.add(config, dependency);
        }
    }

    @Override
    public List<Dependency> getAllDependencies(File zip) {
        try (TempFile config = files.createTempFile(zip.getName(), ".tmp")) {
            copyConfig(zip, config);
            return getManager(zip).getAllDependencies(config);
        }
    }

    private IDependencyManager<Dependency> getManager(File config) {
        return parser.isMaven(config) ? mavenManager : gradleManager;
    }

    private void copyConfig(File src, File dest) {
        files.write(parser.getConfiguration(src).getBytes(), dest);
    }

    @Data
    public static class Dependency {
        private String artifactId;
        private String groupId;
        private String version;
        private String scope = "compile";
    }
}
