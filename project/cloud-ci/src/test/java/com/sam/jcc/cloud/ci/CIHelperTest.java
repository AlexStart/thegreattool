package com.sam.jcc.cloud.ci;

import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static com.sam.jcc.cloud.ci.impl.JenkinsUtil.getJenkins;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Alexey Zhytnik
 * @since 21-Dec-16
 */
public class CIHelperTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public SystemOutRule out = new SystemOutRule().enableLog();

    File project;
    CIHelper helper;

    @Before
    public void setUp() throws Exception {
        project = gradleProjectSources();
        helper = new CIHelper(getJenkins(temp.newFolder()));
    }

    @Test
    public void buildsProject() throws Exception {
        executeWith("create", project);

        executeWith("getStatus", project);
        assertThat(getOut()).isEqualTo("UNKNOWN");

        executeWith("update", project);
        executeWith("getStatus", project);
        assertThat(getOut()).isEqualTo("IN_PROGRESS");
        waitWhileProcessing();
        assertThat(getOut()).isEqualTo("SUCCESSFUL");

        executeWith("getBuild", project);
        try (TempFile build = new TempFile(getOut())) {
            assertThat(isZipFile(build)).isTrue();
        }
        executeWith("delete", project);
    }

    @Test
    public void knowsAboutFail() throws Exception {
        final File project = projectWithFail();

        executeWith("create", project);
        executeWith("update", project);
        waitWhileProcessing();
        assertThat(getOut()).isEqualTo("FAILED");

        try {
            executeWith("getBuild", project);
            fail("should not return failed build");
        } catch (CIBuildNotFoundException ignored) {
        }

        executeWith("delete", project);
    }

    void executeWith(String operation, File project) {
        helper.execute("jenkins", operation, project);
    }

    void waitWhileProcessing() throws Exception {
        do {
            sleep(1_000L);
            out.clearLog();
            helper.execute("jenkins", "getStatus", project);
        } while (out.getLog().equals("IN_PROGRESS"));
    }

    String getOut() {
        final String log = out.getLog();
        out.clearLog();
        return log;
    }

    boolean isZipFile(File file) {
        return new ZipArchiveManager().isZip(file);
    }

    File gradleProjectSources() throws Exception {
        return new ClassPathResource("/gradle-project.zip").getFile();
    }

    File projectWithFail() throws Exception {
        return new ClassPathResource("/wrong-project.zip").getFile();
    }
}
