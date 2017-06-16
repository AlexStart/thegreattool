package com.sam.jcc.cloud.ci.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.exception.CIProjectNotFoundException;
import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataNotFoundException;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;

/**
 * @author Alexey Zhytnik
 * @since 22-Jan-17
 */
@Component
public class CIProjectDao implements ICRUD<CIProject> {

	@Autowired
	private ProjectDataRepository repository;

	@Override
	public CIProject create(CIProject p) {
		final ProjectData data = getOrThrow(p);
		data.setCi(p.getType());
		data.setJobName(p.getName());
		repository.save(data);
		return p;
	}

	@Override
	public CIProject update(CIProject p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CIProject read(CIProject p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(CIProject p) {
		final ProjectData entity = getJobData(p);
		entity.setJobName(null);
		repository.save(entity);
	}

	@Override
	public List<ICIMetadata> findAll() {
		return repository.findByCiNotNull().stream().map(this::convert).collect(toList());
	}

	public boolean exist(CIProject p) {
		return nonNull(getOrThrow(p).getJobName());
	}

	private ProjectData getJobData(CIProject p) {
		final ProjectData entity = getOrThrow(p);

		if (isNull(entity.getJobName())) {
			throw new CIProjectNotFoundException(p);
		}
		return entity;
	}

	private ProjectData getOrThrow(CIProject p) {
		final String artifactId = p.getArtifactId();

		return repository.findByName(artifactId).orElseThrow(() -> new ProjectDataNotFoundException(artifactId));
	}

	private CIProject convert(ProjectData data) {
		final CIProject project = new CIProject();
		project.setArtifactId(data.getName());
		return project;
	}
}
