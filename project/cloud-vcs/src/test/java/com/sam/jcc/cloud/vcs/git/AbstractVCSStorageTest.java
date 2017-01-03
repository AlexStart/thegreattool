package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper.repository;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public abstract class AbstractVCSStorageTest<T extends VCSStorage<?>> {

    protected T server;

    protected VCSRepository repository = repository();

    @Before
    public abstract void setUp();

    @Test
    public void createsAndChecksExistence() throws Exception {
        assertThat(server.isExist(repository)).isFalse();

        server.create(repository);
        sleep(2000L);

        assertThat(server.isExist(repository)).isTrue();
    }

    @After
    public void tearDown() {
        server.delete(repository);
    }
}
