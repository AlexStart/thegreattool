package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.git.impl.storage.AbstractGitServerStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Slf4j
@Component
@Scope("prototype")
public class GitServerVCS extends AbstractVCS implements VCS<VCSCredentials> {

    @Override
    public void read(VCSRepository repo) {
        ((AbstractGitServerStorage) storage).read(repo);
    }

    //TODO - maybe combine VCS + Storage in one object
    @Override
    public void commit(VCSRepository repo) {
        ((AbstractGitServerStorage) storage).commit(repo);
    }

}
