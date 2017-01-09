/**
 * 
 */
package com.sam.jcc.cloud.mvc.controller.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.controller.GenericController;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author Alec Kotovich
 * 
 * TODO
 * 
 */
@Controller
@RequestMapping("/apps")
public class AppController extends GenericController<IProjectMetadata> {

	
	@Autowired
	public AppController(IService<IProjectMetadata> service) {
		super(service);
	}

	@Override
	protected String view() {
		return "apps";
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String post(ModelMap model, @ModelAttribute IProjectMetadata form) {
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
	public String put(ModelMap model, @ModelAttribute IProjectMetadata form) {
		return super.put(model, form);
	}

	@Override
	@RequestMapping(method = RequestMethod.DELETE)
	public String delete(ModelMap model, @ModelAttribute IProjectMetadata entry) {
		return super.delete(model, entry);
	}

	@RequestMapping(value = "/findAndDelete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean findAndDelete(@PathVariable("id") Long id) {
		return true;
	}

	@Override
	protected IProjectMetadata emptyFormMetadata() {
		return null;
	}

	@Override
	protected List<IProjectMetadata> initView(ModelMap model) {
		List<? super IProjectMetadata> models = super.initView(model);
		return null;		
		
	}

}
