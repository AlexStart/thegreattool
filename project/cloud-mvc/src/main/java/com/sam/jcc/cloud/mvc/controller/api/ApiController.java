/**
 * 
 */
package com.sam.jcc.cloud.mvc.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.controller.api.exception.ValidationCloudException;
import com.sam.jcc.cloud.mvc.dto.AppDTO;
import com.sam.jcc.cloud.mvc.dto.ProjectDTO;
import com.sam.jcc.cloud.mvc.service.AppService;
import com.sam.jcc.cloud.mvc.service.ProjectService;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@RestController
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private AppService appService;
	
	@Autowired
	private ProjectService projectService;	

	@Autowired
	private IService<IProjectMetadata> projectProviderService;

	@RequestMapping(value = "apps", method = RequestMethod.GET)
	public @ResponseBody List<? super AppDTO> findAllApps() {
		return appService.findAll();
	}

	@RequestMapping(value = "apps", method = RequestMethod.POST)
	public @ResponseBody AppDTO create(@RequestBody AppDTO appDTO) {
		if (appDTO == null) {
			throw new ValidationCloudException();
		}
		return appService.create(appDTO.getName());
	}

	@RequestMapping(value = "apps", method = RequestMethod.PUT)
	public @ResponseBody AppDTO update(@RequestBody AppDTO appDTO) {
		if (appDTO == null) {
			throw new ValidationCloudException();
		}
		return appService.update(appDTO.getId(), appDTO.getName());
	}

	@RequestMapping(value = "apps/{id}", method = RequestMethod.DELETE)
	public void update(@PathVariable("id") Long id) {
		if (id == null) {
			throw new ValidationCloudException();
		}
		appService.delete(id);
	}

	@RequestMapping(value = "projects", method = RequestMethod.GET)
	public @ResponseBody List<? super IProjectMetadata> findAllProjects() {
		return projectProviderService.findAll();
	}
	
	@RequestMapping(value = "projects", method = RequestMethod.PUT)
	public @ResponseBody ProjectDTO generateProject(@RequestBody ProjectDTO projectDTO) {
		if (projectDTO == null) {
			throw new ValidationCloudException();
		}
		return projectService.update(projectDTO);
	}	

}
