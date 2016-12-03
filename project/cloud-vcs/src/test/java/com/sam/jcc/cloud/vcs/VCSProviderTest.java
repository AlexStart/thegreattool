package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.tool.DirectoryComparator;
import com.sam.jcc.cloud.tool.TempFile;
import com.sam.jcc.cloud.tool.ZipArchiveManager;
import com.sam.jcc.cloud.vcs.git.GitLocalStorage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static com.sam.jcc.cloud.vcs.TestResourceReader.read;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 02.12.2016
 */
public class VCSProviderTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public SystemOutRule out = new SystemOutRule().enableLog();

    VCSProvider vcsProvider;
    ZipArchiveManager zipManager;

    @Before
    public void setUp() throws IOException {
        final GitLocalStorage localGit = new GitLocalStorage();
        localGit.setBaseRepository(temp.newFolder());

        vcsProvider = new VCSProvider(localGit);
        zipManager = new ZipArchiveManager();
    }

    @Test
    public void creates() {
        vcsProvider.execute("git", "file", "create", project());
    }

    @Test
    public void reads() {
        vcsProvider.execute("git", "file", "create", project());
        vcsProvider.execute("git", "file", "read", project());

        try (TempFile zip = new TempFile(out.getLog())) {
            assertThat(zip).exists();
        }
    }

    @Test
    public void deletes() {
        vcsProvider.execute("git", "file", "create", project());
        vcsProvider.execute("git", "file", "delete", project());
    }

    @Test(expected = VCSException.class)
    public void failsOnReadUnknownProject() {
        vcsProvider.execute("git", "file", "read", project());
    }

    @Test(expected = VCSException.class)
    public void failsOnDeleteUnknownProject() {
        vcsProvider.execute("git", "file", "delete", project());
    }

    @Test
    public void worksWithoutChanges() throws IOException {
        vcsProvider.execute("git", "file", "create", project());
        vcsProvider.execute("git", "file", "read", project());

        try (TempFile zip = new TempFile(out.getLog())) {
            checkZipContents(project(), zip);
        }
    }

    @Test
    public void updates() throws IOException {
        vcsProvider.execute("git", "file", "create", project());
        vcsProvider.execute("git", "file", "update", changedProject());
        vcsProvider.execute("git", "file", "read", project());

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
}
