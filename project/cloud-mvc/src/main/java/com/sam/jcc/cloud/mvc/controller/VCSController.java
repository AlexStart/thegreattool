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

import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.mvc.dto.VCSDTO;
import com.sam.jcc.cloud.mvc.service.VCSService;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author Alec Kotovich
 * 
 *         TODO 
 * 
 */
@Controller
@RequestMapping("/vcss")
public class VCSController extends GenericController<VCSDTO, IVCSMetadata> {
	
	@Autowired
	private VCSService vcsService;

	@Autowired
	public VCSController(IService<IVCSMetadata> service, ConversionService conversionService) {
		super(service, conversionService);
	}

	@Override
	protected String view() {
		return "vcss";
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String post(ModelMap model, @ModelAttribute VCSDTO form) {
		return super.post(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String get(ModelMap model) {
		return super.get(model);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getApp(@PathVariable("id") Long id, ModelMap model) {
		return "vcs";
	}

	@Override
	@RequestMapping(method = RequestMethod.GET, params = { "page" })
	public String getPage(ModelMap model, @RequestParam("page") Integer pageNumber) {
		return super.getPage(model, pageNumber);
	}

	@Override
	@RequestMapping(method = RequestMethod.PUT)
	public String put(ModelMap model, @ModelAttribute VCSDTO form) {
		return super.put(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.DELETE)
	public String delete(ModelMap model, @ModelAttribute VCSDTO entry) {
		return super.delete(model, entry);
	}

	@RequestMapping(value = "/findAndDelete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean findAndDelete(@PathVariable("id") Long id) {
		return true;
	}

	@Override
	protected VCSDTO emptyFormMetadata() {
		return new VCSDTO();
	}

	@Override
	protected List<VCSDTO> initView(ModelMap model) {
		List<VCSDTO> models = (List<VCSDTO>) super.initView(model);
		return models;

	}

	@Override
	protected Map<String, String> convertDTO(VCSDTO form) {
		return vcsService.convertDTO(form);
	}

	@Override
	protected VCSDTO convertModel(IVCSMetadata model) {
		return vcsService.convertModel(model);
	}

	@Override
	protected List<? super VCSDTO> convertModels(List<? super IVCSMetadata> models) {
		return vcsService.convertModels(models);
	}

}
