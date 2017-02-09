package com.sam.jcc.cloud.app;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataNotFoundException;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Component
class AppMetadataDao implements ICRUD<AppMetadata> {

	@Autowired
	private ProjectDataRepository repository;

	@Override
	public AppMetadata create(AppMetadata m) {
		final ProjectData data = convert(m);
		repository.save(data);
		m.setId(data.getId());
		return m;
	}

	@Override
	public AppMetadata read(AppMetadata m) {
		return convert(getOrThrow(m));
	}

	@Override
	public AppMetadata update(AppMetadata m) {
		final Long id = get(m).getId();
		m.setId(id);
		repository.save(convert(m));
		return m;
	}

	@Override
	public void delete(AppMetadata m) {
		repository.delete(get(m).getId());
	}

	private ProjectData getOrThrow(AppMetadata metadata) {
		final String name = metadata.getProjectName();
		final Optional<ProjectData> entity = repository.findByName(name);
		return entity.orElseThrow(() -> new ProjectDataNotFoundException(name));
	}

	private ProjectData get(AppMetadata metadata) {
		if (metadata == null || metadata.getId() == null) {
			throw new ProjectDataNotFoundException(metadata.getProjectName());
		}
		final ProjectData entity = repository.findOne(metadata.getId());
		if (entity == null) {
			throw new ProjectDataNotFoundException(metadata.getProjectName());
		} else {
			return entity;
		}
	}

	@Override
	public List<IAppMetadata> findAll() {
		return newArrayList(repository.findAll()).stream().map(this::convert).collect(toList());
	}

	private AppMetadata convert(ProjectData entity) {
		final AppMetadata app = new AppMetadata();
		app.setId(entity.getId());
		app.setProjectName(entity.getName());
		app.setType(entity.getType());
		app.setJobName(entity.getJobName());
		app.setCi(entity.getCi());
		app.setDb(entity.getDb());
		return app;
	}

	private ProjectData convert(AppMetadata app) {
		final ProjectData data = new ProjectData();
		data.setId(app.getId());
		data.setName(app.getProjectName());
		data.setType(app.getType());
		data.setJobName(app.getJobName());
		data.setCi(app.getCi());
		data.setDb(app.getDb());
		return data;
	}
}
