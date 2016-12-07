package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.i.InternalCloudException;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.vcs.VCSCredentialsProvider;
import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSStorage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static java.text.MessageFormat.format;
import static java.util.Optional.empty;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
@Slf4j
abstract class GitAbstractStorage implements VCSStorage<VCSCredentialsProvider> {

    @Getter
    @Setter
    private File baseRepository;

    private FileManager files = new FileManager();

    @Override
    public void create(VCSRepository repo) {
        log.info("Creation of {} repository", repo);
        failOnExist(repo);

        final File dir = repositoryByName(repo);
        log.debug("Repository {} will be placed in {}", repo, dir);

        files.createHiddenDir(dir);
        initBare(dir);
    }

    private void initBare(File dir) {
        log.debug("Init --bare in {}", dir);
        try {
            Git.init()
                    .setDirectory(dir)
                    .setBare(true)
                    .call()
                    .close();
        } catch (GitAPIException e) {
            throw new VCSException(e);
        }
    }

    private void failOnExist(VCSRepository repo) {
        if (isExist(repo)) {
            throw new VCSException(format("Repository {0} exists!", repo.getName()));
        }
    }

    @Override
    public boolean isExist(VCSRepository repo) {
        log.info("Checking existence of {}", repo);
        return repositoryByName(repo).exists();
    }

    @Override
    public void delete(VCSRepository repo) {
        log.info("Delete {}", repo);
        files.delete(get(repo));
    }

    protected File get(VCSRepository repo) {
        final File dir = repositoryByName(repo);
        failOnNotExist(repo);
        return dir;
    }

    private File repositoryByName(VCSRepository repo) {
        return new File(baseRepository, repo.getName());
    }

    private void failOnNotExist(VCSRepository repo) {
        if (!isExist(repo)) {
            throw new VCSException(format("Repository {0} not exist!", repo.getName()));
        }
    }

    @Override
    public Optional<VCSCredentialsProvider> getCredentialsProvider() {
        return empty();
    }

    public void installBaseRepository() {
        log.info("Extracting base repository folder");
        final File home = new File(System.getProperty("user.home"));
        final File base = new File(home, loadBaseRepositoryDirName());

        if (!base.exists()) {
            files.createHiddenDir(base);
        }
        baseRepository = base;
    }

    private String loadBaseRepositoryDirName() {
        final Properties props = new Properties();
        try {
            ClassPathResource resource = new ClassPathResource("vcs.properties");
            props.load(resource.getInputStream());
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
        return props.getProperty("base.repository.folder");
    }
}
