package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.AbstractProvider;
import com.sam.jcc.cloud.i.ICRUD;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.InternalCloudException;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.project.IProjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sam.jcc.cloud.project.ProjectStatus.*;
import static java.lang.String.format;

/**
 * @author Alec Kotovich
 */
@Component
public class ProjectProvider extends AbstractProvider<IProjectMetadata> implements IProjectProvider {

    @Autowired
    private ProjectBuilder builder;
    @Autowired
    private ICRUD<ProjectMetadata> dao;
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
        return dao.create(build(metadata));
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
        srcProcessor.process(project);
        setStatus(m, PROCESSED);
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
        setStatus(m, POST_PROCESSED);
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

    //TODO: add fail on unknown ProjectMetadata
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
        return (List<IProjectMetadata>) dao.findAll();
    }

    private void setStatus(IProjectMetadata m, ProjectStatus status) {
        asProjectMetadata(m).setStatus(status);
    }

    private ProjectMetadata asProjectMetadata(IProjectMetadata metadata) {
        if (!isSupported(metadata)) {
            throw new InternalCloudException("Incorrect execution, in normal case " +
                    "can't execute here, don't support " + metadata);
        }
        return (ProjectMetadata) metadata;
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
}
