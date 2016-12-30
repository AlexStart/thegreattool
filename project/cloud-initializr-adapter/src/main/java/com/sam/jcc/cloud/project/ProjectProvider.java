package com.sam.jcc.cloud.project;

import static com.sam.jcc.cloud.project.ProjectStatus.POST_PROCESSED;
import static com.sam.jcc.cloud.project.ProjectStatus.PRE_PROCESSED;
import static com.sam.jcc.cloud.project.ProjectStatus.PROCESSED;
import static com.sam.jcc.cloud.project.ProjectStatus.UNPROCESSED;
import static java.lang.String.format;

import java.util.List;

import com.sam.jcc.cloud.project.dao.ExtendProjectMetadataDao;
import com.sam.jcc.cloud.provider.UnsupportedTypeException;
import org.springframework.beans.factory.annotation.Autowired;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.project.IProjectProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;

/**
 * @author Alec Kotovich
 */
public abstract class ProjectProvider extends AbstractProvider<IProjectMetadata> implements IProjectProvider {

    @Autowired
    private ProjectBuilder builder;
    @Autowired
    private ExtendProjectMetadataDao dao;
    @Autowired
    private ProjectValidator validator;
    @Autowired
    private TestProcessor testProcessor;
    @Autowired
    private SourceProcessor srcProcessor;

    private String projectType;

    @Autowired
    public ProjectProvider(List<IEventManager<IProjectMetadata>> eventManagers, String projectType) {
        super(eventManagers);
        this.projectType = projectType;
    }

    @Override
    public String getName(IProjectMetadata m) {
        ProjectMetadata metadata = asProjectMetadata(m);
        return format("%s:%s", metadata.getGroupId(), metadata.getArtifactId());
    }

    @Override
    public abstract boolean supports(IProjectMetadata m);

    @Override
    public IProjectMetadata create(IProjectMetadata metadata) {
        return dao.create(build(metadata));
    }

    @Override
    public IProjectMetadata preprocess(IProjectMetadata m) {
        updateStatus(m, UNPROCESSED);
        validator.validate(asProjectMetadata(m));
        updateStatus(m, PRE_PROCESSED);
        return m;
    }

    @Override
    public IProjectMetadata process(IProjectMetadata m) {
        final ProjectMetadata project = asProjectMetadata(m);
        srcProcessor.process(project);
        updateStatus(m, PROCESSED);
        return m;
    }

    @Override
    public IProjectMetadata postprocess(IProjectMetadata m) {
        final ProjectMetadata metadata = asProjectMetadata(m);
        try {
            testProcessor.process(metadata);
            builder.build(metadata);
        } catch (Exception e) {
            builder.reset(metadata);
            throw e;
        }
        updateStatus(m, POST_PROCESSED);
        return m;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IProjectMetadata read(IProjectMetadata m) {
        return dao.read(asProjectMetadata(m));
    }

    @Override
    public IProjectMetadata update(IProjectMetadata metadata) {
        return dao.update(build(metadata));
    }

    private ProjectMetadata build(IProjectMetadata metadata) {
        final IProjectMetadata created = super.create(metadata);
        return asProjectMetadata(created);
    }

    @Override
    public void delete(IProjectMetadata m) {
        dao.delete(asProjectMetadata(m));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<IProjectMetadata> findAll() {
        return (List<IProjectMetadata>) dao.findAllByProjectType(projectType);
    }

    private void updateStatus(IProjectMetadata project, ProjectStatus status) {
        asProjectMetadata(project).setStatus(status);
        eventManagers.forEach(manager -> manager.fireEvent(project, this));
    }

    private ProjectMetadata asProjectMetadata(IProjectMetadata metadata) {
        if (!supports(metadata)) {
            throw new UnsupportedTypeException(metadata);
        }
        return (ProjectMetadata) metadata;
    }
}

