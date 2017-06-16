package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.git.impl.GitCredentials;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.of;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Slf4j
@Component
@Setter(PROTECTED)
@Scope("prototype")
public abstract class AbstractGitServerVCS extends AbstractVCS implements VCS<VCSCredentials> {

    private String user;
    private String password;

    @Override
    public Optional<VCSCredentials> getCredentialsProvider() {
        return of(new GitCredentials(user, password));
    }

    public abstract void commit(VCSRepository repo);

    public abstract void read(VCSRepository repo);
}
