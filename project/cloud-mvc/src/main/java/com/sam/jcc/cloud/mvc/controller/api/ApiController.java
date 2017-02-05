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
import com.sam.jcc.cloud.mvc.dto.CIProjectDTO;
import com.sam.jcc.cloud.mvc.dto.DbProjectDTO;
import com.sam.jcc.cloud.mvc.dto.ProjectDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.mvc.dto.VCSProjectDTO;
import com.sam.jcc.cloud.mvc.service.AppService;
import com.sam.jcc.cloud.mvc.service.CIProjectService;
import com.sam.jcc.cloud.mvc.service.CIService;
import com.sam.jcc.cloud.mvc.service.DbService;
import com.sam.jcc.cloud.mvc.service.ProjectService;
import com.sam.jcc.cloud.mvc.service.VCSProjectService;
import com.sam.jcc.cloud.mvc.service.VCSService;

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
	private VCSService vcsService;

	@Autowired
	private VCSProjectService vcsProjectService;

	@Autowired
	private CIService ciService;

	@Autowired
	private CIProjectService ciProjectService;
	
	@Autowired
	private DbService dbService;	

	// APPS //
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

	// PROJECTS //
	@RequestMapping(value = "projects", method = RequestMethod.GET)
	public @ResponseBody List<? super IProjectMetadata> findAllProjects() {
		return projectService.findAll();
	}

	@RequestMapping(value = "projects", method = RequestMethod.PUT)
	public @ResponseBody ProjectDTO generateProject(@RequestBody ProjectDTO projectDTO) {
		if (projectDTO == null) {
			throw new ValidationCloudException();
		}
		return projectService.update(projectDTO);
	}

	// PROVIDERS
	@RequestMapping(value = "providers", method = RequestMethod.GET)
	public @ResponseBody List<ProviderDTO> findAllProviders() {
		return projectService.getProjectProviders();
	}

	// VCS PROVIDERS
	@RequestMapping(value = "vcsproviders", method = RequestMethod.GET)
	public @ResponseBody List<ProviderDTO> findAllVcsProviders() {
		return vcsService.getVCSProviders();
	}

	@RequestMapping(value = "vcsprojects", method = RequestMethod.PUT)
	public @ResponseBody VCSProjectDTO addprojectToVCS(@RequestBody VCSProjectDTO vcsProjectDTO) {
		if (vcsProjectDTO == null) {
			throw new ValidationCloudException();
		}
		return vcsProjectService.update(vcsProjectDTO);
	}

	// CI PROVIDERS
	@RequestMapping(value = "ciproviders", method = RequestMethod.GET)
	public @ResponseBody List<ProviderDTO> findAllCiProviders() {
		return ciService.getCIProviders();
	}

	@RequestMapping(value = "ciprojects", method = RequestMethod.PUT)
	public @ResponseBody CIProjectDTO addprojectToCI(@RequestBody CIProjectDTO ciProjectDTO) {
		if (ciProjectDTO == null) {
			throw new ValidationCloudException();
		}
		return ciProjectService.update(ciProjectDTO);
	}

	// DB PROVIDERS
	@RequestMapping(value = "dbproviders", method = RequestMethod.GET)
	public @ResponseBody List<ProviderDTO> findAllDbProviders() {
		return dbService.getDbProviders();
	}
	
	@RequestMapping(value = "dbprojects", method = RequestMethod.PUT)
	public @ResponseBody CIProjectDTO addprojectToDb(@RequestBody DbProjectDTO dbProjectDTO) {
		return null;
	}

}
