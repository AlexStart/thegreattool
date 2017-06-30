/**
 * 
 */
package com.sam.jcc.cloud.mvc.controller.api;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import com.sam.jcc.cloud.mvc.dto.CQProjectDTO;
import com.sam.jcc.cloud.mvc.dto.DbProjectDTO;
import com.sam.jcc.cloud.mvc.dto.ProjectDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.mvc.dto.VCSProjectDTO;
import com.sam.jcc.cloud.mvc.service.AppService;
import com.sam.jcc.cloud.mvc.service.CIProjectService;
import com.sam.jcc.cloud.mvc.service.CIService;
import com.sam.jcc.cloud.mvc.service.CQProjectService;
import com.sam.jcc.cloud.mvc.service.CQService;
import com.sam.jcc.cloud.mvc.service.DbProjectService;
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

	@Autowired
	private DbProjectService dbProjectService;

	@Autowired
	private CQService cqService;

	@Autowired
	private CQProjectService cqProjectService;

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

	@RequestMapping(value = "projects/{id}", method = RequestMethod.GET, produces = "application/zip")
	public byte[] getProjectSource(@PathVariable("id") Long id, HttpServletResponse response) {
		IProjectMetadata projectMetadata = projectService.findProjectById(id);
		if (projectMetadata != null && projectMetadata.hasSources()) {

			// setting headers
			response.setContentType("application/zip");
			response.setStatus(HttpServletResponse.SC_OK);
			response.addHeader("Content-Disposition",
					"attachment; filename=\"" + projectMetadata.getName() + ".zip" + "\"");

			byte[] src = projectMetadata.getProjectSources();
			return src;
		}
		return null;
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
	public @ResponseBody DbProjectDTO addprojectToDb(@RequestBody DbProjectDTO dbProjectDTO) {
		if (dbProjectDTO == null) {
			throw new ValidationCloudException();
		}
		return dbProjectService.update(dbProjectDTO);
	}

	// CODE QUALITY PROVIDERS
	@RequestMapping(value = "cqproviders", method = RequestMethod.GET)
	public @ResponseBody List<ProviderDTO> findAllCQProviders() {
		return cqService.getCQProviders();
	}

	@RequestMapping(value = "cqprojects", method = RequestMethod.PUT)
	public @ResponseBody CQProjectDTO addprojectToCQ(@RequestBody CQProjectDTO cqProjectDTO) {
		if (cqProjectDTO == null) {
			throw new ValidationCloudException();
		}
		return cqProjectService.update(cqProjectDTO);
	}

}
