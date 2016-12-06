package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.tool.FileManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSRepositoryDataHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@RunWith(Parameterized.class)
public class GitVCSTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    GitVCS git;
    String protocol;
    VCSRepository repository;

    /* automatic managed */
    Process gitDaemon;

    public GitVCSTest(String protocol) {
        this.git = new GitVCS();
        this.protocol = protocol;
        this.repository = VCSRepositoryDataHelper.repository();
    }

    @Parameters
    @SuppressWarnings("RedundantArrayCreation")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][]{
                {"git"},
                {"file"}
        });
    }

    @Before
    public void setUp() throws IOException {
        final GitLocalStorage storage = new GitLocalStorage();
        storage.setBaseRepository(temp.newFolder());
        storage.setProtocol(protocol);
        git.setStorage(storage);

        if (isGitProtocolActive()) {
            gitDaemon = new GitDaemonRunner().run(storage.getBaseRepository());
        }
    }

    @After
    public void tearDown() {
        if (isGitProtocolActive()) {
            new ProcessKiller().kill(gitDaemon);
        }
    }

    @Test
    public void createsAndDeletes() throws IOException {
        git.create(repository);
        git.delete(repository);
    }

    @Test
    public void reads() throws IOException {
        git.create(repository);
        final Entry<String, byte[]> data = writeSomeDataAndCommit();

        final File dest = temp.newFolder();
        repository.setSources(dest);

        git.read(repository);
        assertThat(dest.listFiles()).isNotNull().isNotEmpty();

        final File copy = new File(dest, data.getKey());

        assertThat(copy)
                .exists()
                .isFile()
                .hasBinaryContent(data.getValue());
    }

    @Test
    public void worksStable() throws IOException {
        git.create(repository);

        writeSomeDataAndCommit();
        writeSomeDataAndCommit();

        repository.setSources(temp.newFolder());
        git.read(repository);
    }

    @Test
    public void checksExistence() {
        assertThat(git.isExist(repository)).isFalse();

        git.create(repository);

        assertThat(git.isExist(repository)).isTrue();
    }

    @Test
    public void commits() throws IOException {
        git.create(repository);
        writeSomeDataAndCommit();
    }

    @Test
    public void integrationTest() throws IOException {
        assertThat(git.isExist(repository)).isFalse();
        git.create(repository);
        assertThat(git.isExist(repository)).isTrue();

        repository.setSources(temp.newFolder());
        writeSomeDataAndCommit();

        repository.setSources(temp.newFolder());
        git.read(repository);

        assertThat(repository.getSources().listFiles())
                .isNotNull()
                .isNotEmpty();

        git.delete(repository);
        assertThat(git.isExist(repository)).isFalse();
    }

    boolean isGitProtocolActive() {
        return protocol.equals("git");
    }

    Entry<String, byte[]> writeSomeDataAndCommit() throws IOException {
        final Random random = new Random();
        final File src = temp.newFolder();
        final File file = new File(src, random.nextInt() + "-file.txt");

        final byte[] content = new byte[10_000];
        random.nextBytes(content);
        new FileManager().write(content, file);

        repository.setSources(src);

        git.commit(repository);

        return entry(file.getName(), content);
    }
}