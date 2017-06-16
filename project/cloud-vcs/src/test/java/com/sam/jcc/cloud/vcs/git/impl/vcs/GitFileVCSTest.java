package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryAlreadyExistsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public class GitFileVCSTest extends AbstractVCSTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    final private GitFileVCS vcs;

    public GitFileVCSTest() {
        super(new GitFileVCS());
        this.vcs = (GitFileVCS) super.vcs;
    }

    @Before
    public void setUp() throws IOException {
        vcs.setBaseRepository(temp.newFolder());
        setTemp(temp);
    }

    @After
    public void tearDown() {
        if (vcs.isExist(repository)) {
            vcs.delete(repository);
        }
    }

    @Test(expected = VCSRepositoryAlreadyExistsException.class)
    public void failsOnCreationExistence() {
        vcs.create(repository);
        vcs.create(repository);
    }

    @Test
    public void installsBaseDirectory() throws IOException {
        final File parent = temp.newFolder();

        System.setProperty("user.home", parent.toString());
        vcs.installBaseRepository();

        assertThat(vcs.getBaseRepository()).hasParent(parent);
    }

    @Test
    public void checkFilesWhenCreateDeletes() {
        vcs.create(repository);
        final File dir = getFile(repository);
        assertThat(dir).isNotNull().exists();

        vcs.delete(repository);
        assertThat(dir).doesNotExist();
    }

    @Test
    public void commitRead() throws IOException {
        vcs.create(repository);
        final File dest = temp.newFolder();
        repository.setSources(dest);

        Map.Entry<String, byte[]> data = writeSomeBinaryDataAndCommit();
        repository.setSources(dest);
        vcs.read(repository);

        assertThat(dest.listFiles()).isNotNull().isNotEmpty();
        final File copy = new File(dest, data.getKey());
        assertThat(copy).exists().isFile().hasBinaryContent(data.getValue());
    }

    private File getFile(VCSRepository repository) {
        String uri = vcs.getRepositoryURI(repository);
        return new FileManager().getFileByUri(uri);
    }
}