package com.sam.jcc.cloud.vcs;

import static com.sam.jcc.cloud.utils.files.FileManager.getResource;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import com.sam.jcc.cloud.utils.files.DirectoryComparator;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.vcs.git.impl.storage.GitFileStorage;

/**
 * @author Alexey Zhytnik
 * @since 02.12.2016
 */
public class VCSHelperTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public SystemOutRule out = new SystemOutRule().enableLog();

    VCSHelper vcsHelper;
    ZipArchiveManager zipManager;

    @Before
    public void setUp() throws IOException {
        final GitFileStorage localGit = new GitFileStorage();
        localGit.setBaseRepository(temp.newFolder());

        vcsHelper = new VCSHelper(localGit);
        zipManager = new ZipArchiveManager();
    }

    @Test
    public void creates() {
        vcsHelper.execute("git", "file", "create", project());
    }

    @Test
    public void reads() {
        vcsHelper.execute("git", "file", "create", project());
        vcsHelper.execute("git", "file", "read", project());

        try (TempFile zip = new TempFile(out.getLog())) {
            assertThat(zip).exists();
        }
    }

    @Test
    public void deletes() {
        vcsHelper.execute("git", "file", "create", project());
        vcsHelper.execute("git", "file", "delete", project());
    }

    @Test(expected = VCSRepositoryNotFoundException.class)
    public void failsOnReadUnknownProject() {
        vcsHelper.execute("git", "file", "read", project());
    }

    @Test(expected = VCSRepositoryNotFoundException.class)
    public void failsOnDeleteUnknownProject() {
        vcsHelper.execute("git", "file", "delete", project());
    }

    @Test
    public void worksWithoutChanges() throws IOException {
        vcsHelper.execute("git", "file", "create", project());
        vcsHelper.execute("git", "file", "read", project());

        try (TempFile zip = new TempFile(out.getLog())) {
            checkZipContents(project(), zip);
        }
    }

    @Test
    public void updates() throws IOException {
        vcsHelper.execute("git", "file", "create", project());
        vcsHelper.execute("git", "file", "update", changedProject());
        vcsHelper.execute("git", "file", "read", project());

        try (TempFile zip = new TempFile(out.getLog())) {
            checkZipContents(changedProject(), zip);
        }
    }

    void checkZipContents(File realZip, File copyZip) throws IOException {
        final File in = temp.newFolder();
        final File out = temp.newFolder();

        zipManager.unzip(realZip, in);
        zipManager.unzip(copyZip, out);

        assertThat(new DirectoryComparator().areEquals(in, out)).isTrue();
    }

    File project() {
        return read("/project.zip");
    }

    File changedProject() {
        return read("/changed_project.zip");
    }

    File read(String resource) {
        return getResource(getClass(), resource);
    }
}
