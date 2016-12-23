package com.sam.jcc.cloud.ci.util;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 22-Dec-16
 */
public final class CIProjectTemplates {

    private CIProjectTemplates() {
    }

    public static CIProject loadProject(String type, File projectDir) throws Exception {
        return setUpProject(type, projectDir);
    }

    public static CIProject projectWithFailedTest(File projectDir) throws Exception {
        return setUpProject("wrong", projectDir);
    }

    private static CIProject setUpProject(String type, File target) throws Exception {
        final CIProject p = new CIProject();
        p.setArtifactId("TempProject-" + type);

        final String pathToSrc = format("/{0}-project.zip", type);
        p.setSources(copyProjectSourcesInto(pathToSrc, target));
        return p;
    }

    private static File copyProjectSourcesInto(String src, File target) throws Exception {
        final File sources = new ClassPathResource(src).getFile();
        new ZipArchiveManager().unzip(sources, target);
        return target;
    }
}
