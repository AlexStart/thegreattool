package com.sam.jcc.cloud.utils.project;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.utils.project.DependencyManager.Dependency;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.builder.AstBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;

/**
 * @author Alexey Zhytnik
 * @since 13.01.2017
 */
//TODO: maybe there's simple way...
class GradleDependencyManager implements IDependencyManager<Dependency> {

    @Override
    public List<Dependency> getAllDependencies(File config) {
        return visit(config).getDependencies();
    }

    @Override
    public String add(File config, Dependency dependency) {
        final List<String> content = toList(config);
        final GradleBuildVisitor visitor = visit(config);
        final String line = asString(dependency);

        if (visitor.getColumnNum() != -1) {
            StringBuilder builder = new StringBuilder(content.get(visitor.getDependenceLineNum() - 1));
            builder.insert(visitor.getColumnNum() - 2, "\n" + line + "\n");
            String dep = builder.toString();

            content.remove(visitor.getDependenceLineNum() - 1);
            content.add(visitor.getDependenceLineNum() - 1, dep);
        } else {
            content.add(visitor.getDependenceLineNum() - 1, line);
        }

        return String.join("\n", content);
    }

    private String asString(Dependency dependency) {
        return String.format("\t%s group: \"%s\", name:\"%s\", version:\"%s\"",
                dependency.getScope(),
                dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getVersion()
        );
    }

    private GradleBuildVisitor visit(File config) {
        final GradleBuildVisitor visitor = new GradleBuildVisitor();
        final List<ASTNode> nodes = new AstBuilder().buildFromString(toString(config));
        walkScript(visitor, nodes);
        return visitor;
    }

    private void walkScript(GroovyCodeVisitor visitor, List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            node.visit(visitor);
        }
    }

    private String toString(File file) {
        try {
            final URL url = file.toURI().toURL();
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }

    private List<String> toList(File file) {
        try {
            return Files.readLines(file, UTF_8);
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }
}
