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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sam.jcc.cloud.i.project.Status.*;

/**
 * @author Alec Kotovich
 */
@Component
public class ProjectProvider extends AbstractProvider<IProjectMetadata> implements IProjectProvider {

    @Autowired
    private ProjectBuilder builder;

    @Autowired
    private ProjectValidator validator;

    @Autowired
    public ProjectProvider(List<IEventManager<IProjectMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    public String getName(IProjectMetadata m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supports(IProjectMetadata m) {
        return isSupported(m);
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
        final byte[] sources = builder.build(project);
        project.setProjectSources(sources);
        setStatus(m, PROCESSED);
        return m;
    }

    @Override
    public IProjectMetadata postprocess(IProjectMetadata m) {
        setStatus(m, POST_PROCESSED);
        return m;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IProjectMetadata read(IProjectMetadata m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProjectMetadata update(IProjectMetadata m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(IProjectMetadata m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IProjectMetadata> findAll() {
        throw new UnsupportedOperationException();
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
