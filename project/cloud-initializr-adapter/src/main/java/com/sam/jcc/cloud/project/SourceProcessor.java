package com.sam.jcc.cloud.project;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@Component
@Slf4j
class SourceProcessor {

	@Autowired
	private ProjectGenerator generator;

	@Autowired
	private MetadataToRequestConverter converter;

	public void process(ProjectMetadata project) {
		final ProjectRequest request = converter.convert(project);
		log.info("Converted to project request: " + request);
		File srcDir = generator.generateProjectStructure(request);
		log.info("Generated project structure in " + srcDir.getAbsolutePath());
		project.setDirectory(srcDir);
	}
}
