package com.sam.jcc.cloud.vcs.git.impl;

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
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.*;

/**
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

    protected abstract GitAbstractStorage getStorage();

    @Override
    public boolean supports(IVCSMetadata metadata) {
        return metadata instanceof VCSRepository;
    }

    @Override
    public IVCSMetadata read(IVCSMetadata m) {
        final VCSRepository repo = asVCSRepository(m);
        dao.read(repo);
        git.read(repo);

        updateStatus(repo, CLONED);
        return repo;
    }

    @Override
    public IVCSMetadata update(IVCSMetadata m) {
        final VCSRepository repo = asVCSRepository(m);
        dao.update(repo);
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
        final VCSRepository repo = asVCSRepository(m);
        git.create(repo);
        dao.create(repo);
        updateStatus(repo, CREATED);
        return m;
    }

    @Override
    public IVCSMetadata postprocess(IVCSMetadata m) {
        return asVCSRepository(m);
    }

    private void updateStatus(IVCSMetadata m, VCSRepositoryStatus status) {
        asVCSRepository(m).setStatus(status);
        notify(m);
    }

    private VCSRepository asVCSRepository(IVCSMetadata metadata) {
        if (!supports(metadata)) {
            throw new UnsupportedTypeException(metadata);
        }
        return (VCSRepository) metadata;
    }
}
