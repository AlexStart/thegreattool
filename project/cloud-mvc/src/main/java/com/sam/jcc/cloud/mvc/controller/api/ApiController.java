/**
 * 
 */
package com.sam.jcc.cloud.mvc.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.AppDTO;
import com.sam.jcc.cloud.mvc.service.AppService;
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
	private IService<IAppMetadata> appProviderService;

	@Autowired
	private IService<IProjectMetadata> projectProviderService;

	@RequestMapping(value = "apps", method = RequestMethod.GET)
	public @ResponseBody List<? super AppDTO> findAllApps() {
		return appService.convertModels(appProviderService.findAll());
	}

	@RequestMapping(value = "projects", method = RequestMethod.GET)
	public @ResponseBody List<? super IProjectMetadata> findAllProjects() {
		return projectProviderService.findAll();
	}

}
