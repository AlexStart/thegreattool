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

import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.mvc.dto.CIDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.mvc.service.CIService;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author Alec Kotovich
 * 
 *         TODO
 * 
 */
@Controller
@RequestMapping("/cis")
public class CIController extends GenericController<CIDTO, ICIMetadata> {

	@Autowired
	private CIService ciService;

	@Autowired
	public CIController(IService<ICIMetadata> ciProviderService, ConversionService conversionService) {
		super(ciProviderService, conversionService);
	}

	@Override
	protected String view() {
		return "cis";
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String post(ModelMap model, @ModelAttribute CIDTO form) {
		return super.post(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String get(ModelMap model) {
		List<ProviderDTO> models = ciService.getCIProviders();
		model.addAttribute("models", models);
		return view();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getApp(@PathVariable("id") Long id, ModelMap model) {
		return "ci";
	}

	@Override
	@RequestMapping(method = RequestMethod.GET, params = { "page" })
	public String getPage(ModelMap model, @RequestParam("page") Integer pageNumber) {
		return super.getPage(model, pageNumber);
	}

	@Override
	@RequestMapping(method = RequestMethod.PUT)
	public String put(ModelMap model, @ModelAttribute CIDTO form) {
		return super.put(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.DELETE)
	public String delete(ModelMap model, @ModelAttribute CIDTO entry) {
		return super.delete(model, entry);
	}

	@RequestMapping(value = "/findAndDelete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean findAndDelete(@PathVariable("id") Long id) {
		return true;
	}

	@Override
	protected CIDTO emptyFormMetadata() {
		return new CIDTO();
	}

	@Override
	protected Map<String, String> convertDTO(CIDTO form) {
		return ciService.convertDTO(form);
	}

	@Override
	protected CIDTO convertModel(ICIMetadata model) {
		return ciService.convertModel(model);
	}

	@Override
	protected List<? super CIDTO> convertModels(List<? super ICIMetadata> models) {
		return ciService.convertModels(models);
	}

}
