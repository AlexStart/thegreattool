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

import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.mvc.dto.DbDTO;
import com.sam.jcc.cloud.mvc.dto.ProviderDTO;
import com.sam.jcc.cloud.mvc.service.DbService;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author Alec Kotovich
 * 
 *         TODO
 * 
 */
@Controller
@RequestMapping("/dbs")
public class DatabaseController extends GenericController<DbDTO, IDataMetadata> {

	@Autowired
	private DbService dbService;

	@Autowired
	public DatabaseController(IService<IDataMetadata> dbProviderService, ConversionService conversionService) {
		super(dbProviderService, conversionService);
	}

	@Override
	protected String view() {
		return "dbs";
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String post(ModelMap model, @ModelAttribute DbDTO form) {
		return super.post(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String get(ModelMap model) {
		List<ProviderDTO> models = dbService.getDbProviders();
		model.addAttribute("models", models);
		return view();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getApp(@PathVariable("id") Long id, ModelMap model) {
		return "db";
	}

	@Override
	@RequestMapping(method = RequestMethod.GET, params = { "page" })
	public String getPage(ModelMap model, @RequestParam("page") Integer pageNumber) {
		return super.getPage(model, pageNumber);
	}

	@Override
	@RequestMapping(method = RequestMethod.PUT)
	public String put(ModelMap model, @ModelAttribute DbDTO form) {
		return super.put(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.DELETE)
	public String delete(ModelMap model, @ModelAttribute DbDTO entry) {
		return super.delete(model, entry);
	}

	@RequestMapping(value = "/findAndDelete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean findAndDelete(@PathVariable("id") Long id) {
		return true;
	}

	@Override
	protected DbDTO emptyFormMetadata() {
		return new DbDTO();
	}

	@Override
	protected Map<String, String> convertDTO(DbDTO form) {
		return dbService.convertDTO(form);
	}

	@Override
	protected DbDTO convertModel(IDataMetadata model) {
		return dbService.convertModel(model);
	}

	@Override
	protected List<? super DbDTO> convertModels(List<? super IDataMetadata> models) {
		return dbService.convertModels(models);
	}

}
