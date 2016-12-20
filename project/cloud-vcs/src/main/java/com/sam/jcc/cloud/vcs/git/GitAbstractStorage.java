package com.sam.jcc.cloud.vcs.git;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.util.Optional.empty;

import java.io.File;
import java.util.Optional;

import com.sam.jcc.cloud.utils.files.ItemStorage;
import com.sam.jcc.cloud.utils.files.ItemStorage.ItemAlreadyExistsException;
import com.sam.jcc.cloud.utils.files.ItemStorage.ItemNotFoundException;
import com.sam.jcc.cloud.vcs.exception.VCSDuplicateRepositoryException;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSStorage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
@Slf4j
abstract class GitAbstractStorage implements VCSStorage<VCSCredentials> {

    protected final ItemStorage<VCSRepository> storage;

    public GitAbstractStorage() {
        storage = new ItemStorage<>(VCSRepository::getName);
    }

    @Override
    public void create(VCSRepository repo) {
        try {
            initBare(storage.create(repo));
        } catch (ItemAlreadyExistsException e) {
            throw new VCSDuplicateRepositoryException(repo);
        }
    }

    private void initBare(File dir) {
        log.debug("Init --bare in {}", dir);
        try {
            Git.init().setDirectory(dir).setBare(true).call().close();
        } catch (GitAPIException e) {
            throw new VCSException(e);
        }
    }

    @Override
    public boolean isExist(VCSRepository repo) {
        return storage.isExist(repo);
    }

    @Override
    public void delete(VCSRepository repo) {
        try {
            storage.delete(repo);
        } catch (ItemNotFoundException e) {
            throw new VCSRepositoryNotFoundException(repo);
        }
    }

    @Override
    public Optional<VCSCredentials> getCredentialsProvider() {
        return empty();
    }

    public void installBaseRepository() {
        final File base = new File(getProperty("repository.base.folder"));
        setBaseRepository(base);
    }

    public void setBaseRepository(File dir) {
        storage.setRoot(dir);
    }

    public File getBaseRepository(){
        return storage.getRoot();
    }
}
