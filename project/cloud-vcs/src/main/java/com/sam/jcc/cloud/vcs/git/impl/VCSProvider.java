package com.sam.jcc.cloud.vcs.git.impl;

import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.CLONED;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.COMMITED;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.CREATED;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.DELETED;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.PUSHED;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.provider.UnsupportedTypeException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSRepositoryStatus;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryAlreadyExistsException;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * TODO remove git-related code to a new class AbstractGitProvider
 * 
 * @author olegk
 * @author Alexey Zhytnik
 */
public abstract class VCSProvider extends AbstractProvider<IVCSMetadata> implements IVCSProvider {

    @Getter
    @Autowired
    @VisibleForTesting
    private GitVCS git;

    @Setter
    @Autowired
    @VisibleForTesting
    private GitMetadataDao dao;

    @Autowired
    public VCSProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
        super(eventManagers);
    }

    @PostConstruct
    public void setUp() {
        final GitAbstractStorage storage = getStorage();
        storage.installBaseRepository();
        git.setStorage(storage);
    }

    //TODO(a bad part of the app): the injection should be changed by @Autowired + @Qualifier
    protected abstract GitAbstractStorage getStorage();

    @Override
    public boolean supports(IVCSMetadata m) {
        return m instanceof VCSRepository;
    }

    @Override
    public IVCSMetadata read(IVCSMetadata m) {
        git.read(asVCSRepository(m));
        updateStatus(m, CLONED);
        return m;
    }

    @Override
    public IVCSMetadata update(IVCSMetadata m) {
        final VCSRepository repo = asVCSRepository(m);

        git.commit(repo);
        updateStatus(repo, COMMITED);
        updateStatus(repo, PUSHED);
        return repo;
    }

    @Override
    public void delete(IVCSMetadata m) {
        final VCSRepository repo = asVCSRepository(m);
        git.delete(repo);
        dao.delete(repo);
        updateStatus(repo, DELETED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<? super IVCSMetadata> findAll() {
        return (List<IVCSMetadata>) (List<?>) git.getAllRepositories();
    }

    @Override
    public IVCSMetadata preprocess(IVCSMetadata m) {
        final VCSRepository repo = asVCSRepository(m);
        if (dao.exist(repo)) {
            throw new VCSRepositoryAlreadyExistsException(repo);
        }
        return repo;
    }

    @Override
    public IVCSMetadata process(IVCSMetadata m) {
        git.create(asVCSRepository(m));
        return m;
    }

    @Override
    public IVCSMetadata postprocess(IVCSMetadata m) {
        dao.create(asVCSRepository(m));
        updateStatus(m, CREATED);
        return m;
    }

    protected void updateStatus(IVCSMetadata m, VCSRepositoryStatus status) {
        asVCSRepository(m).setStatus(status);
        notify(m);
    }

    protected VCSRepository asVCSRepository(IVCSMetadata metadata) {
        if (!supports(metadata)) {
            throw new UnsupportedTypeException(metadata);
        }
        return (VCSRepository) metadata;
    }
}
