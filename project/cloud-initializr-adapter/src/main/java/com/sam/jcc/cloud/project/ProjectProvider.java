/**
 *
 */
package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.AbstractProvider;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.InternalCloudException;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.project.IProjectProvider;
import com.sam.jcc.cloud.i.project.Status;
import com.sam.jcc.cloud.persistence.project.ProjectMetadataEntity;
import com.sam.jcc.cloud.persistence.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.sam.jcc.cloud.i.project.Status.*;
import static com.sam.jcc.cloud.project.ZipUtil.archivateDir;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

/**
 * @author Alec Kotovich
 */
@Component
public class ProjectProvider extends AbstractProvider<IProjectMetadata> implements IProjectProvider {

    @Autowired
    private Cleaner cleaner;
    @Autowired
    private ProjectValidator validator;
    @Autowired
    private TestGenerator testGenerator;
    @Autowired
    private SourceGenerator srcGenerator;
    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectEntityConverter entityConverter;
    @Autowired
    private ProjectMetadataConverter metadataConverter;

    @Autowired
    public ProjectProvider(List<IEventManager<IProjectMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    public String getName(IProjectMetadata m) {
        ProjectMetadata metadata = asProjectMetadata(m);
        return format("%s:%s", metadata.getGroupId(), metadata.getArtifactId());
    }

    @Override
    public boolean supports(IProjectMetadata m) {
        return isSupported(m);
    }

    @Override
    public IProjectMetadata create(IProjectMetadata metadata) {
        final IProjectMetadata created = super.create(metadata);
        final ProjectMetadataEntity entity = metadataConverter.convert(asProjectMetadata(created));
        repository.save(entity);
        return created;
    }

    @Override
    public IProjectMetadata preprocess(IProjectMetadata m) {
        setStatus(m, UNPROCESSED);
        validator.validate(asProjectMetadata(m));
        setStatus(m, PRE_PROCESSED);
        return m;
    }

    @Override
    public IProjectMetadata process(IProjectMetadata m) {
        final ProjectMetadata project = asProjectMetadata(m);
        project.setDirectory(srcGenerator.generate(project));
        setStatus(m, PROCESSED);
        return m;
    }

    @Override
    public IProjectMetadata postprocess(IProjectMetadata m) {
        final ProjectMetadata metadata = asProjectMetadata(m);
        try {
            testGenerator.generate(metadata);
            archivate(metadata);
        } finally {
            cleaner.remove(metadata.getDirectory());
        }
        setStatus(m, POST_PROCESSED);
        return m;
    }

    private void archivate(ProjectMetadata metadata) {
        byte[] sources = archivateDir(metadata.getDirectory());
        metadata.setProjectSources(sources);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IProjectMetadata read(IProjectMetadata m) {
        ProjectMetadata metadata = asProjectMetadata(m);
        return entityConverter.convert(search(metadata));
    }

    @Override
    public IProjectMetadata update(IProjectMetadata m) {
        delete(m);
        return create(m);
    }

    @Override
    public void delete(IProjectMetadata m) {
        ProjectMetadata metadata = asProjectMetadata(m);
        repository.delete(search(metadata));
    }

    private ProjectMetadataEntity search(ProjectMetadata metadata) {
        return repository.findByGroupIdAndArtifactId(
                metadata.getGroupId(),
                metadata.getArtifactId()
        );
    }

    @Override
    public List<IProjectMetadata> findAll() {
        return newArrayList(repository.findAll())
                .stream()
                .map(entityConverter::convert)
                .collect(toList());
    }

    private boolean isSupported(IProjectMetadata metadata) {
        if (!(metadata instanceof ProjectMetadata)) return false;

        final ProjectMetadata m = (ProjectMetadata) metadata;
        final String name = m.getProjectType();
        return isGradleOrMaven(name);
    }

    private boolean isGradleOrMaven(String name) {
        return name != null && (name.equals("maven-project") || name.equals("gradle-project"));
    }

    private void setStatus(IProjectMetadata m, Status status) {
        asProjectMetadata(m).setStatus(status);
    }

    private ProjectMetadata asProjectMetadata(IProjectMetadata metadata) {
        if (!isSupported(metadata)) {
            throw new InternalCloudException("Incorrect execution, in normal case " +
                    "can't execute here, don't support " + metadata);
        }
        return (ProjectMetadata) metadata;
    }
}
