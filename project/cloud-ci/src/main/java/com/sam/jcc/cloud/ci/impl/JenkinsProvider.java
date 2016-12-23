package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.CIProjectStatus;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIServerNotAvailableException;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.i.ci.ICIProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.provider.UnsupportedTypeException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

import static com.sam.jcc.cloud.ci.CIProjectStatus.CREATED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.DELETED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_NO_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.UPDATED;

/**
 * @author Alexey Zhytnik
 * @since 22-Dec-16
 */
@Component
public class JenkinsProvider extends AbstractProvider<ICIMetadata> implements ICIProvider {

    @Setter
    @Autowired
    @Getter(AccessLevel.PACKAGE)
    private Jenkins jenkins;

    public JenkinsProvider(List<IEventManager<ICIMetadata>> iEventManagers) {
        super(iEventManagers);
    }

    @Override
    public boolean supports(ICIMetadata m) {
        return m instanceof CIProject;
    }

    @Override
    public boolean isEnabled() {
        return jenkins.isEnabled();
    }

    @Override
    public ICIMetadata preprocess(ICIMetadata m) {
        return asCIProject(m);
    }

    //TODO: maybe change creation from pre-,pro-, post- phases to command-way.
    //      For example, execute list of commands, where a command is java.util.function.Function.
    @Override
    public ICIMetadata process(ICIMetadata m) {
        checkAccess();
        final CIProject project = asCIProject(m);

        jenkins.create(project);
        updateStatus(project, CREATED);

        jenkins.build(asCIProject(m));
        updateStatus(m, UPDATED);

        return project;
    }

    @Override
    public ICIMetadata postprocess(ICIMetadata m) {
        return asCIProject(m);
    }

    @Override
    public ICIMetadata read(ICIMetadata m) {
        checkAccess();
        final CIProject project = asCIProject(m);

        try {
            final InputStream build = jenkins.getLastSuccessfulBuild(project);
            project.setBuild(build);
            updateStatus(project, HAS_BUILD);
        } catch (CIBuildNotFoundException e) {
            updateStatus(project, HAS_NO_BUILD);
            throw e;
        }
        return project;
    }

    @Override
    public ICIMetadata update(ICIMetadata project) {
        checkAccess();
        jenkins.build(asCIProject(project));
        updateStatus(project, UPDATED);
        return project;
    }

    @Override
    public void delete(ICIMetadata project) {
        checkAccess();
        jenkins.delete(asCIProject(project));
        updateStatus(project, DELETED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ICIMetadata> findAll() {
        final List<CIProject> projects = jenkins.getAllProjects();
        return (List<ICIMetadata>) (List<?>) projects;
    }

    private void checkAccess() {
        if (!jenkins.isEnabled()) {
            throw new CIServerNotAvailableException();
        }
    }

    private void updateStatus(ICIMetadata project, CIProjectStatus status) {
        asCIProject(project).setStatus(status);
        eventManagers.forEach(manager -> manager.fireEvent(project, this));
    }

    private CIProject asCIProject(ICIMetadata metadata) {
        if (!supports(metadata)) {
            throw new UnsupportedTypeException(metadata);
        }
        return (CIProject) metadata;
    }
}
