package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.git.impl.storage.GitlabServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class GitlabVCSTest extends AbstractVCSTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    public GitlabVCSTest() {
        super(new GitServerVCS());
    }

    @Before
    public void setUp() throws IOException {
        vcs.setStorage(new GitlabServer());
        setTemp(temp);
    }

    @After
    public void tearDown() {
        if (vcs.isExist(repository)) {
            vcs.delete(repository);
        }
    }
}