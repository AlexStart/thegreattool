package com.sam.jcc.cloud.utils.project;

import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.utils.files.FileManager;
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

    @Setter
    @Autowired
    private ProjectParser parser;

    private IDependencyManager<Dependency> mavenManager = new MavenDependencyManager();
    private IDependencyManager<Dependency> gradleManager = new GradleDependencyManager();

    @Override
    public String add(File sources, Dependency dependency) {
        final IDependencyManager<Dependency> manager = getManager(sources);
        final File config = parser.getConfiguration(sources);

        final List<Dependency> dependencies = manager.getAllDependencies(config);
        if (dependencies.contains(dependency)) throw new InternalCloudException();

        final String updatedConfig = manager.add(config, dependency);

        files.write(updatedConfig.getBytes(), config);
        return updatedConfig;
    }

    @Override
    public List<Dependency> getAllDependencies(File sources) {
        final File config = parser.getConfiguration(sources);
        return getManager(sources).getAllDependencies(config);
    }

    private IDependencyManager<Dependency> getManager(File config) {
        return parser.isMaven(config) ? mavenManager : gradleManager;
    }

    @Data
    public static class Dependency {
        private String artifactId;
        private String groupId;
        private String version;
        private String scope = "compile";
    }
}
