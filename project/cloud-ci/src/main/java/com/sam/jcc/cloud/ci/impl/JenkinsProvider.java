package com.sam.jcc.cloud.ci.impl;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.CIProjectStatus;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIProjectAlreadyExistsException;
import com.sam.jcc.cloud.ci.exception.CIServerNotAvailableException;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.i.ci.ICIProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.provider.UnsupportedTypeException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private static final long JENKINS_PROVIDER_ID = 5L;

    @Setter
    @Autowired(required = false)
    @VisibleForTesting
    private Jenkins jenkins;

    @Setter
    @Autowired
    @VisibleForTesting
    private CIProjectDao dao;

    public JenkinsProvider(List<IEventManager<ICIMetadata>> eventManagers) {
        super(eventManagers);
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
        final CIProject project = asCIProject(m);
        if (dao.exist(project)) {
            throw new CIProjectAlreadyExistsException(project);
        }
        return project;
    }

    @Override
    public ICIMetadata process(ICIMetadata m) {
        checkAccess();

        final CIProject project = asCIProject(m);
        jenkins.create(project);
        jenkins.build(project);
        return project;
    }

    @Override
    public ICIMetadata postprocess(ICIMetadata m) {
        dao.create(asCIProject(m));
        updateStatus(m, CREATED);
        return m;
    }

    @Override
    public ICIMetadata read(ICIMetadata m) {
        checkAccess();

        final CIProject project = asCIProject(m);
        try {
            final byte[] build = jenkins.getLastSuccessfulBuild(project);
            project.setBuild(build);
            updateStatus(project, HAS_BUILD);
        } catch (CIBuildNotFoundException e) {
            updateStatus(project, HAS_NO_BUILD);
            throw e;
        }
        return project;
    }

    @Override
    public ICIMetadata update(ICIMetadata m) {
        checkAccess();

        jenkins.build(asCIProject(m));
        updateStatus(m, UPDATED);
        return m;
    }

    @Override
    public void delete(ICIMetadata m) {
        checkAccess();
        final CIProject project = asCIProject(m);

        jenkins.delete(project);
        dao.delete(project);
        updateStatus(project, DELETED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ICIMetadata> findAll() {
        checkAccess();
        return (List<ICIMetadata>) (List<?>) jenkins.getAllProjects();
    }

    private void checkAccess() {
        if (!jenkins.isEnabled()) {
            throw new CIServerNotAvailableException();
        }
    }

    private void updateStatus(ICIMetadata project, CIProjectStatus status) {
        asCIProject(project).setStatus(status);
        notify(project);
    }

    private CIProject asCIProject(ICIMetadata metadata) {
        if (!supports(metadata)) {
            throw new UnsupportedTypeException(metadata);
        }
        return (CIProject) metadata;
    }

	@Override
	public Long getId() {
		return JENKINS_PROVIDER_ID;
	}
}
