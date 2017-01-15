package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.provider.UnsupportedTypeException;
import com.sam.jcc.cloud.vcs.git.GitAbstractStorage;
import com.sam.jcc.cloud.vcs.git.GitVCS;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.*;

/**
 * @author olegk
 * @author Alexey Zhytnik
 */
public abstract class VCSProvider extends AbstractProvider<IVCSMetadata> implements IVCSProvider {

    @Autowired
    private GitVCS git;

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
    public IVCSMetadata read(IVCSMetadata repo) {
        git.read(asVCSRepository(repo));
        updateStatus(repo, CLONED);
        return repo;
    }

    @Override
    public IVCSMetadata update(IVCSMetadata repo) {
        git.commit(asVCSRepository(repo));
        updateStatus(repo, COMMITED);
        updateStatus(repo, PUSHED);
        return repo;
    }

    @Override
    public void delete(IVCSMetadata repo) {
        git.delete(asVCSRepository(repo));
        updateStatus(repo, DELETED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<? super IVCSMetadata> findAll() {
        return (List<IVCSMetadata>) (List<?>) git.getAllRepositories();
    }

    @Override
    public IVCSMetadata preprocess(IVCSMetadata m) {
        return asVCSRepository(m);
    }

    @Override
    public IVCSMetadata process(IVCSMetadata repo) {
        git.create(asVCSRepository(repo));
        updateStatus(repo, CREATED);
        return repo;
    }

    @Override
    public IVCSMetadata postprocess(IVCSMetadata m) {
        return asVCSRepository(m);
    }

    //TODO: common functionality
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
