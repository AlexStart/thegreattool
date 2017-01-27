package com.sam.jcc.cloud.project;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;

/**
 * @author Alexey Zhytnik
 * @since 23.11.2016
 */
@Component
class ProjectMetadataDao implements ICRUD<ProjectMetadata> {

	@Autowired
	private ProjectDataRepository repository;

	@Override
	public ProjectMetadata create(ProjectMetadata m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProjectMetadata read(ProjectMetadata m) {
		return convert(getOrThrow(m));
	}

	@Override
	public ProjectMetadata update(ProjectMetadata m) {
		final ProjectData data = getOrThrow(m);

		data.setSources(m.getProjectSources());
		repository.save(data);
		return m;
	}

	@Override
	public void delete(ProjectMetadata m) {
		repository.delete(getOrThrow(m));
	}

	@Override
	public List<IProjectMetadata> findAll() {
		return repository.findBySourcesNotNull().stream().map(this::convert).collect(toList());
	}

	private ProjectData getOrThrow(ProjectMetadata metadata) {
		final String name = metadata.getArtifactId();
		final Optional<ProjectData> data = repository.findByName(name);

		return data.orElseThrow(() -> new ProjectNotFoundException(metadata));
	}

	private ProjectMetadata convert(ProjectData data) {
		final ProjectMetadata metadata = new ProjectMetadata();

		metadata.setId(data.getId());
		metadata.setGroupId(data.getGroupId());
		metadata.setArtifactId(data.getName());
		metadata.setProjectName(metadata.getArtifactId());
		metadata.setProjectSources(data.getSources());
		metadata.setVcs(data.getVcs());

		return metadata;
	}
}
