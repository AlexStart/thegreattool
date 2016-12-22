package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static com.sam.jcc.cloud.ci.impl.Jenkins.defaultJenkinsServer;

/**
 * @author Alexey Zhytnik
 * @since 22-Dec-16
 */
public final class JenkinsUtil {

    private JenkinsUtil() {
    }

    //TODO: maybe change to another Jenkins server
    public static Jenkins getJenkins(File workspace) {
        return new Jenkins(defaultJenkinsServer(), workspace);
    }

    public static CIProject correctProject(File projectDir) throws Exception {
        return setUpProject("/maven-project.zip", projectDir);
    }

    public static CIProject projectWithFailedTest(File projectDir) throws Exception {
        return setUpProject("/wrong-project.zip", projectDir);
    }

    private static CIProject setUpProject(String pathToSrc, File target) throws Exception {
        final CIProject p = new CIProject();
        p.setArtifactId("TempProject");
        p.setSources(copyProjectSourcesInto(pathToSrc, target));
        return p;
    }

    private static File copyProjectSourcesInto(String src, File target) throws Exception {
        final File sources = new ClassPathResource(src).getFile();
        new ZipArchiveManager().unzip(sources, target);
        return target;
    }
}
