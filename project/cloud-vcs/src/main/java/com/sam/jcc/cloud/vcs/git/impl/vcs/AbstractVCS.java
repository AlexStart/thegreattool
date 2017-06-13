package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSStorage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Slf4j
@Component
@Scope("prototype")
public abstract class AbstractVCS implements VCS<VCSCredentials> {

    @Setter
    @Getter
    protected VCSStorage<VCSCredentials> storage;

    @Override
    public boolean isExist(VCSRepository repo) {
        return storage.isExist(repo);
    }

    @Override
    public void delete(VCSRepository repo) {
        storage.delete(repo);
    }

    @Override
    public void create(VCSRepository repo) {
        storage.create(repo);
    }

    @Override
    public List<VCSRepository> getAllRepositories() {
        return storage.getAllRepositories();
    }
}
