package com.sam.jcc.cloud.vcs.git.impl.storage;

import com.sam.jcc.cloud.utils.files.ItemStorage;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSStorage;
import com.sam.jcc.cloud.vcs.git.impl.VCSRepositoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static java.util.Optional.empty;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
@Slf4j
public abstract class AbstractStorage implements VCSStorage<VCSCredentials> {

    protected final ItemStorage<VCSRepository> storage;

    public AbstractStorage() {
        storage = new ItemStorage<>(VCSRepository::getName, new VCSRepositoryBuilder());
    }

    @Override
    public Optional<VCSCredentials> getCredentialsProvider() {
        return empty();
    }
}
