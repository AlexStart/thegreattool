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

import com.sam.jcc.cloud.i.cd.ICDMetadata;
import com.sam.jcc.cloud.mvc.dto.CDDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.mvc.service.CDService;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author Alec Kotovich
 * 
 *         TODO
 * 
 */
@Controller
@RequestMapping("/cds")
public class CDController extends GenericController<CDDTO, ICDMetadata> {

	@Autowired
	private CDService cdService;

	@Autowired
	public CDController(IService<ICDMetadata> cdProviderService, ConversionService conversionService) {
		super(cdProviderService, conversionService);
	}

	@Override
	protected String view() {
		return "cds";
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String post(ModelMap model, @ModelAttribute CDDTO form) {
		return super.post(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String get(ModelMap model) {
		List<ProviderDTO> models = cdService.getCDProviders();
		model.addAttribute("models", models);
		return view();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getApp(@PathVariable("id") Long id, ModelMap model) {
		return "cd";
	}

	@Override
	@RequestMapping(method = RequestMethod.GET, params = { "page" })
	public String getPage(ModelMap model, @RequestParam("page") Integer pageNumber) {
		return super.getPage(model, pageNumber);
	}

	@Override
	@RequestMapping(method = RequestMethod.PUT)
	public String put(ModelMap model, @ModelAttribute CDDTO form) {
		return super.put(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.DELETE)
	public String delete(ModelMap model, @ModelAttribute CDDTO entry) {
		return super.delete(model, entry);
	}

	@RequestMapping(value = "/findAndDelete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean findAndDelete(@PathVariable("id") Long id) {
		return true;
	}

	@Override
	protected CDDTO emptyFormMetadata() {
		return new CDDTO();
	}

	@Override
	protected Map<String, String> convertDTO(CDDTO form) {
		return cdService.convertDTO(form);
	}

	@Override
	protected CDDTO convertModel(ICDMetadata model) {
		return cdService.convertModel(model);
	}

	@Override
	protected List<? super CDDTO> convertModels(List<? super ICDMetadata> models) {
		return cdService.convertModels(models);
	}

}
