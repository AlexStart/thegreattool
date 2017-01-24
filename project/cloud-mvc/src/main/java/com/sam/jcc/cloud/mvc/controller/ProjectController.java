/**
 * 
 */
package com.sam.jcc.cloud.mvc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.dto.ProjectDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.mvc.service.ProjectService;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author Alec Kotovich
 * 
 *         TODO 
 * 
 */
@Controller
@RequestMapping("/projects")
public class ProjectController extends GenericController<ProjectDTO, IProjectMetadata> {
	
	@Autowired
	private ProjectService projectService;

	@Autowired
	public ProjectController(IService<IProjectMetadata> service, ConversionService conversionService) {
		super(service, conversionService);
	}

	@Override
	protected String view() {
		return "projects";
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String post(ModelMap model, @ModelAttribute ProjectDTO form) {
		return super.post(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String get(ModelMap model) {
		List<ProviderDTO> models = projectService.getProjectProviders();
		model.addAttribute("models", models);
		return view();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getApp(@PathVariable("id") Long id, ModelMap model) {
		return "project";
	}

	@Override
	@RequestMapping(method = RequestMethod.GET, params = { "page" })
	public String getPage(ModelMap model, @RequestParam("page") Integer pageNumber) {
		return super.getPage(model, pageNumber);
	}

	@Override
	@RequestMapping(method = RequestMethod.PUT)
	public String put(ModelMap model, @ModelAttribute ProjectDTO form) {
		return super.put(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.DELETE)
	public String delete(ModelMap model, @ModelAttribute ProjectDTO entry) {
		return super.delete(model, entry);
	}

	@RequestMapping(value = "/findAndDelete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean findAndDelete(@PathVariable("id") Long id) {
		return true;
	}

	@Override
	protected ProjectDTO emptyFormMetadata() {
		return new ProjectDTO();
	}


	@Override
	protected Map<String, String> convertDTO(ProjectDTO form) {
		return projectService.convertDTO(form);
	}

	@Override
	protected ProjectDTO convertModel(IProjectMetadata model) {
		return projectService.convertModel(model);
	}

	@Override
	protected List<? super ProjectDTO> convertModels(List<? super IProjectMetadata> models) {
		return projectService.convertModels(models);
	}

}
