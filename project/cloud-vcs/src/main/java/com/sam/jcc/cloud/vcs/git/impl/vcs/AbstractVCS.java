package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.utils.files.ItemStorage;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.git.impl.VCSRepositoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.empty;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Slf4j
@Component
@Scope("prototype")
public abstract class AbstractVCS implements VCS<VCSCredentials> {

    protected final ItemStorage<VCSRepository> storage;

    public AbstractVCS() {
        storage = new ItemStorage<>(VCSRepository::getName, new VCSRepositoryBuilder());
    }

    @Override
    public Optional<VCSCredentials> getCredentialsProvider() {
        return empty();
    }
}
