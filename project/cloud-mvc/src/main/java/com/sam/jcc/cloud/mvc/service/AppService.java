/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.i.project.DummyProjectMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.AppDTO;
import com.sam.jcc.cloud.rules.service.IService;
import com.sam.jcc.cloud.utils.project.ArtifactIdValidator;

/**
 * @author olegk
 *
 */
@Service
public class AppService extends BaseService<AppDTO> {

	private static Logger LOGGER = LoggerFactory.getLogger(AppService.class);

	@Autowired
	private IService<IAppMetadata> appProviderService;

	@Autowired
	private IService<IProjectMetadata> projectProviderService;

	@Autowired
	private ArtifactIdValidator nameValidator;

	public AppDTO create(String appName) {
		nameValidator.validate(appName);
		Map<String, String> props = new HashMap<>();
		props.put("projectName", appName);
		props.put("type", "none");
		return convertModel(appProviderService.create(props));
	}

	public AppDTO convertModel(IAppMetadata model) {
		return conversionService.convert(model, AppDTO.class);
	}

	public List<? super AppDTO> convertModels(List<? super IAppMetadata> models) {
		List<AppDTO> dtos = new ArrayList<>();
		models.forEach(m -> dtos.add(conversionService.convert(m, AppDTO.class)));
		return dtos;
	}

	public List<? super AppDTO> findAll() {
		return convertModels(appProviderService.findAll());
	}

	public AppDTO update(Long id, String appName) {
		nameValidator.validate(appName);
		Map<String, String> props = new HashMap<>();
		props.put("id", String.valueOf(id));
		props.put("projectName", appName);
		return convertModel(appProviderService.update(props));

	}

	public void delete(Long id) {
		IProjectMetadata read = projectProviderService.read(new DummyProjectMetadata(id));

		if (read.hasSources()) {
			LOGGER.warn("Deleting a project " + read.getName() + " with existing sources...");
		}

		if (read.hasVCS() || read.hasCI() || read.hasDb()) {
			LOGGER.warn("Cannot be deleted!");
			return;
		}

		Map<String, String> props = new HashMap<>();
		props.put("id", String.valueOf(id));
		appProviderService.delete(props);
	}

}
