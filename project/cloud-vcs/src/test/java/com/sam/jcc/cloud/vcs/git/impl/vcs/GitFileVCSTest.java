package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.git.impl.storage.GitFileStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

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
}