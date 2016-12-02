package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.tool.DirectoryComparator;
import com.sam.jcc.cloud.tool.FileManager;
import com.sam.jcc.cloud.tool.ZipArchiveManager;
import com.sam.jcc.cloud.vcs.git.GitLocalStorage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
    public void creates() throws Exception {
        vcsProvider.execute("git", "file", "create", project());
    }

    @Test
    public void reads() throws Exception {
        vcsProvider.execute("git", "file", "create", project());
        vcsProvider.execute("git", "file", "read", project());

        final File zip = new File(out.getLog());
        assertThat(zip).exists();

        new FileManager().delete(zip);
    }

    @Test
    public void worksWithoutChanges() throws Exception {
        vcsProvider.execute("git", "file", "create", project());
        vcsProvider.execute("git", "file", "read", project());

        final File zip = new File(out.getLog());

        try {
            final File in = temp.newFolder();
            final File out = temp.newFolder();

            zipManager.unzip(zip, out);
            zipManager.unzip(project(), in);

            assertThat(new DirectoryComparator().areEquals(in, out)).isTrue();
        } finally {
            new FileManager().delete(zip);
        }
    }

    @Test
    public void deletes() throws Exception {
        vcsProvider.execute("git", "file", "create", project());
        vcsProvider.execute("git", "file", "delete", project());
    }

    @Test(expected = VCSException.class)
    public void failsOnReadUnknownProject() throws Exception {
        vcsProvider.execute("git", "file", "read", project());
    }

    @Test(expected = VCSException.class)
    public void failsOnDeleteUnknownProject() throws Exception {
        vcsProvider.execute("git", "file", "delete", project());
    }

    File project() throws Exception {
        final URL resource = this.getClass().getResource("/project.zip");
        return new File(resource.toURI());
    }
}
