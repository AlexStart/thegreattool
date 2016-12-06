package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper.repository;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 01.12.2016
 */
public class GitLocalStorageTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    VCSRepository repository = repository();

    GitLocalStorage repos;

    @Before
    public void setUp() throws Exception {
        repos = new GitLocalStorage();
        repos.setBaseRepository(temp.newFolder());
    }

    @Test
    public void installsBaseDirectory() throws IOException {
        final File parent = temp.newFolder();

        System.setProperty("user.home", parent.toString());
        repos.installBaseRepository();

        assertThat(repos.getBaseRepository()).hasParent(parent);
    }

    @Test
    public void creates() {
        repos.create(repository);
        assertThat(getFile(repository)).exists();
    }

    @Test
    public void createsBaseDirectory() {
        repos.create(repository);

        final File place = getFile(repository);

        assertThat(place)
                .hasParent(repos.getBaseRepository())
                .hasName(repository.getName());
    }

    @Test(expected = VCSException.class)
    public void failsOnCreationExistence() {
        repos.create(repository);
        repos.create(repository);
    }

    @Test(expected = VCSException.class)
    public void failsOnDeleteNotExistence() {
        repos.delete(repository);
    }

    @Test
    public void gets() {
        repos.create(repository);
        assertThat(getFile(repository)).isNotNull().exists();
    }

    @Test
    public void checksExistence() {
        assertThat(repos.isExist(repository)).isFalse();

        repos.create(repository);

        assertThat(repos.isExist(repository)).isTrue();
    }

    @Test
    public void deletes() {
        repos.create(repository);
        final File dir = getFile(repository);

        repos.delete(repository);
        assertThat(dir).doesNotExist();
    }

    File getFile(VCSRepository repository) {
        String uri = repos.getRepositoryURI(repository);
        return new FileManager().getFileByUri(uri);
    }
}