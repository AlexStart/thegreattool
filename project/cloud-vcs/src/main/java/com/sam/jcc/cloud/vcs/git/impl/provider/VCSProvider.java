package com.sam.jcc.cloud.vcs.git.impl.provider;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.provider.UnsupportedTypeException;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSRepositoryStatus;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryAlreadyExistsException;
import com.sam.jcc.cloud.vcs.git.impl.GitMetadataDao;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.*;
import static java.util.Objects.requireNonNull;

/**
 * @author olegk
 * @author Alexey Zhytnik
 */
public abstract class VCSProvider extends AbstractProvider<IVCSMetadata> implements IVCSProvider {

    @Getter
    @VisibleForTesting
    protected VCS vcs;

    @Setter
    @Autowired
    @VisibleForTesting
    private GitMetadataDao dao;

    @Autowired
    public VCSProvider(List<IEventManager<IVCSMetadata>> eventManagers, VCS vcs) {
        super(eventManagers);
        this.vcs = requireNonNull(vcs);
    }

    @Override
    public boolean supports(IVCSMetadata m) {
        return m instanceof VCSRepository;
    }

    @Override
    public IVCSMetadata read(IVCSMetadata m) {
        vcs.read(asVCSRepository(m));
        updateStatus(m, CLONED);
        return m;
    }

    @Override
    public IVCSMetadata update(IVCSMetadata m) {
        final VCSRepository repo = asVCSRepository(m);

        vcs.commit(repo);
        updateStatus(repo, COMMITED);
        updateStatus(repo, PUSHED);
        return repo;
    }

    @Override
    public void delete(IVCSMetadata m) {
        final VCSRepository repo = asVCSRepository(m);
        vcs.delete(repo);
        dao.delete(repo);
        updateStatus(repo, DELETED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<? super IVCSMetadata> findAll() {
        return (List<IVCSMetadata>) (List<?>) vcs.getAllRepositories();
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
        vcs.create(asVCSRepository(m));
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
