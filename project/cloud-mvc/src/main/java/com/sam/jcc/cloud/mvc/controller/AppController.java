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

import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.mvc.dto.AppDTO;
import com.sam.jcc.cloud.mvc.service.AppService;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author Alec Kotovich
 * 
 *         TODO
 * 
 */
@Controller
@RequestMapping("/apps")
public class AppController extends GenericController<AppDTO, IAppMetadata> {
	
	@Autowired
	private AppService appService;

	@Autowired
	public AppController(IService<IAppMetadata> service, ConversionService conversionService) {
		super(service, conversionService);
	}

	@Override
	protected String view() {
		return "apps";
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String post(ModelMap model, @ModelAttribute AppDTO form) {
		return super.post(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String get(ModelMap model) {
		return super.get(model);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getApp(@PathVariable("id") Long id, ModelMap model) {
		return "app";
	}

	@Override
	@RequestMapping(method = RequestMethod.GET, params = { "page" })
	public String getPage(ModelMap model, @RequestParam("page") Integer pageNumber) {
		return super.getPage(model, pageNumber);
	}

	@Override
	@RequestMapping(method = RequestMethod.PUT)
	public String put(ModelMap model, @ModelAttribute AppDTO form) {
		return super.put(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.DELETE)
	public String delete(ModelMap model, @ModelAttribute AppDTO entry) {
		return super.delete(model, entry);
	}

	@RequestMapping(value = "/findAndDelete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean findAndDelete(@PathVariable("id") Long id) {
		return true;
	}

	@Override
	protected AppDTO emptyFormMetadata() {
		return new AppDTO();
	}

	@Override
	protected List<AppDTO> initView(ModelMap model) {
		List<AppDTO> models = (List<AppDTO>) super.initView(model);
		return models;

	}

	@Override
	protected Map<String, String> convertDTO(AppDTO form) {
		return appService.convertDTO(form);
	}

	@Override
	protected AppDTO convertModel(IAppMetadata model) {
		return appService.convertModel(model);
	}

	@Override
	protected List<? super AppDTO> convertModels(List<? super IAppMetadata> models) {
		return appService.convertModels(models);
	}

}
