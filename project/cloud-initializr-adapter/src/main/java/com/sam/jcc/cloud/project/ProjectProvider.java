package com.sam.jcc.cloud.project;

import static com.sam.jcc.cloud.project.ProjectStatus.POST_PROCESSED;
import static com.sam.jcc.cloud.project.ProjectStatus.PRE_PROCESSED;
import static com.sam.jcc.cloud.project.ProjectStatus.PROCESSED;
import static com.sam.jcc.cloud.project.ProjectStatus.UNPROCESSED;
import static java.lang.String.format;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.project.IProjectProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
import com.sam.jcc.cloud.provider.UnsupportedTypeException;

/**
 * @author Alec Kotovich
 */
public abstract class ProjectProvider extends AbstractProvider<IProjectMetadata> implements IProjectProvider {

    @Autowired
    private ProjectBuilder builder;
    @Autowired
    private ProjectMetadataDao dao;
    @Autowired
    private ProjectValidator validator;
    @Autowired
    private TestProcessor testProcessor;
    @Autowired
    private SourceProcessor srcProcessor;

    @Autowired
    public ProjectProvider(List<IEventManager<IProjectMetadata>> eventManagers) {
        super(eventManagers);
    }

    //TODO(a bad part of the app): not used
    @Override
    public String getName(IProjectMetadata m) {
        ProjectMetadata metadata = asProjectMetadata(m);
        return format("%s:%s", metadata.getGroupId(), metadata.getArtifactId());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public abstract boolean supports(IProjectMetadata m);

    @Override
    public IProjectMetadata create(IProjectMetadata metadata) {
    	throw new UnsupportedCallException();
    }

    private ProjectMetadata build(IProjectMetadata metadata) {
        final IProjectMetadata created = super.create(metadata);
        return asProjectMetadata(created);
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
    //TODO(a bad part of the app): in this phase should work with byte[] representation
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
    public IProjectMetadata read(IProjectMetadata m) {
        return dao.read(asProjectMetadata(m));
    }

    @Override
    public IProjectMetadata update(IProjectMetadata metadata) {
        return dao.update(build(metadata));
    }

    @Override
    public void delete(IProjectMetadata m) {
        throw new UnsupportedCallException();
    }

    @Override
    public List<IProjectMetadata> findAll() {
        return dao.findAll();
    }

    private void updateStatus(IProjectMetadata project, ProjectStatus status) {
        asProjectMetadata(project).setStatus(status);
        notify(project);
    }

    private ProjectMetadata asProjectMetadata(IProjectMetadata metadata) {
        if (!supports(metadata)) {
            throw new UnsupportedTypeException(metadata);
        }
        return (ProjectMetadata) metadata;
    }

	public abstract String getType();
}
