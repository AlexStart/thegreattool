package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.persistence.data.EntityNotFoundException;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 22-Jan-17
 */
@Component
public class GitMetadataDao implements ICRUD<VCSRepository> {

    @Autowired
    private ProjectDataRepository repository;

    @Override
    public VCSRepository create(VCSRepository p) {
        final ProjectData entity = getOrThrow(p);
        entity.setVcs(p.getName());
        repository.save(entity);
        return p;
    }

    @Override
    public VCSRepository update(VCSRepository p) {
        return convert(getVcsData(p));
    }

    @Override
    public VCSRepository read(VCSRepository p) {
        return convert(getVcsData(p));
    }

    @Override
    public void delete(VCSRepository p) {
        final ProjectData entity = getVcsData(p);
        entity.setCi(null);
        repository.save(entity);
    }

    @Override
    public List<IVCSMetadata> findAll() {
        return repository.findByVcsNotNull()
                .stream()
                .map(this::convert)
                .collect(toList());
    }

    public boolean exist(VCSRepository p) {
        return nonNull(getOrThrow(p).getVcs());
    }

    private ProjectData getVcsData(VCSRepository p) {
        final ProjectData entity = getOrThrow(p);

        if (isNull(entity.getVcs())) {
            throw new VCSRepositoryNotFoundException(p);
        }
        return entity;
    }

    private ProjectData getOrThrow(VCSRepository p) {
        final String name = p.getArtifactId();

        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(p));
    }

    private VCSRepository convert(ProjectData data) {
        final VCSRepository repo = new VCSRepository();
        repo.setArtifactId(data.getName());
        repo.setName(data.getVcs());
        return repo;
    }
}
