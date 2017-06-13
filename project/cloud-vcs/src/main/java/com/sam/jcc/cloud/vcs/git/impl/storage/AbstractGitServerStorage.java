package com.sam.jcc.cloud.vcs.git.impl.storage;

import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.git.impl.GitCredentials;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static java.util.Optional.of;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
@Slf4j
@Setter(PROTECTED)
public abstract class AbstractGitServerStorage extends AbstractStorage {

    private String user;
    private String password;

    @Override
    public Optional<VCSCredentials> getCredentialsProvider() {
        return of(new GitCredentials(user, password));
    }

    public abstract void commit(VCSRepository repo);

    public abstract void read(VCSRepository repo);
}
