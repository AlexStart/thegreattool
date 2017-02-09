package com.sam.jcc.cloud.vcs.git.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataNotFoundException;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;

/**
 * @author Alexey Zhytnik
 * @since 22-Jan-17
 */
@Component
class GitMetadataDao implements ICRUD<VCSRepository> {

    @Autowired
    private ProjectDataRepository repository;

    @Override
    public VCSRepository create(VCSRepository p) {
        final ProjectData data = getOrThrow(p);
        data.setVcs(p.getVcsType());
        data.setVcsName(p.getName());
        repository.save(data);
        return p;
    }

    @Override
    public VCSRepository update(VCSRepository p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VCSRepository read(VCSRepository p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(VCSRepository p) {
        final ProjectData entity = getVcsData(p);
        entity.setJobName(null);
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
        return nonNull(getOrThrow(p).getVcsName());
    }

    private ProjectData getVcsData(VCSRepository p) {
        final ProjectData entity = getOrThrow(p);

        if (isNull(entity.getVcsName())) {
            throw new VCSRepositoryNotFoundException(p);
        }
        return entity;
    }

    private ProjectData getOrThrow(VCSRepository p) {
        final String artifactId = p.getArtifactId();

        return repository.findByName(artifactId)
                .orElseThrow(() -> new ProjectDataNotFoundException(artifactId));
    }

    private VCSRepository convert(ProjectData data) {
        final VCSRepository repo = new VCSRepository();
        repo.setArtifactId(data.getName());
        repo.setVcsType(data.getVcs());
        return repo;
    }
}
