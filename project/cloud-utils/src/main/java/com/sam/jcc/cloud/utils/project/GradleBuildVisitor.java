package com.sam.jcc.cloud.utils.project;

import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.utils.project.DependencyManager.Dependency;
import lombok.Getter;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.*;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Getter
@Experimental("work solution")
class GradleBuildVisitor extends CodeVisitorSupport {

    private int dependenceLineNum = -1;
    private int columnNum = -1;
    private List<Dependency> dependencies = newArrayList();

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        if (!(call.getMethodAsString().equals("buildscript"))) {
            if (call.getMethodAsString().equals("dependencies")) {
                if (dependenceLineNum == -1) {
                    dependenceLineNum = call.getLastLineNumber();
                }
            }
            super.visitMethodCallExpression(call);
        }
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression ale) {
        List<Expression> expressions = ale.getExpressions();

        if (expressions.size() == 1 && expressions.get(0) instanceof ConstantExpression) {
            String depStr = expressions.get(0).getText();
            String[] deps = depStr.split(":");

            if (deps.length == 3) {
                dependencies.add(dependency(deps[0], deps[1], deps[2]));
            } else if (deps.length == 2) {
                dependencies.add(dependency(deps[0], deps[1], null));
            }
        }

        super.visitArgumentlistExpression(ale);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        if (dependenceLineNum != -1 && expression.getLineNumber() == expression.getLastLineNumber()) {
            columnNum = expression.getLastColumnNumber();
        }
        super.visitClosureExpression(expression);
    }

    @Override
    public void visitMapExpression(MapExpression expression) {
        final Map<String, String> dependency = newHashMap();

        if (expression.getMapEntryExpressions().size() == 3) {
            for (MapEntryExpression mapEntryExpression : expression.getMapEntryExpressions()) {
                final String key = mapEntryExpression.getKeyExpression().getText();
                final String value = mapEntryExpression.getValueExpression().getText();
                dependency.put(key, value);
            }
            dependencies.add(dependency(dependency));
        }
        super.visitMapExpression(expression);
    }

    private Dependency dependency(Map<String, String> map) {
        return dependency(map.get("group"), map.get("name"), map.get("version"));
    }

    private Dependency dependency(String group, String name, String version) {
        final Dependency d = new Dependency();

        d.setGroupId(group);
        d.setArtifactId(name);
        d.setVersion(version);
        return d;
    }
}
