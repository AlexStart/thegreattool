package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.git.impl.storage.GitFileStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public class GitFileVCSTest extends AbstractVCSTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    public GitFileVCSTest() {
        super(new GitVCS());
    }

    @Before
    public void setUp() throws IOException {
        final GitFileStorage storage = new GitFileStorage();
        storage.setBaseRepository(temp.newFolder());
        vcs.setStorage(storage);
        setTemp(temp);
    }

    @After
    public void tearDown() {
        if (vcs.isExist(repository)) {
            vcs.delete(repository);
        }
    }

    @Override
    public Object writeToFileToCommit(File file) throws IOException {
        return writeRandomStringToFile(file);
    }

    @Override
    public void checkFileContent(File file, Object content) throws IOException {
        assertThat(file).hasContent((String) content);
    }
}