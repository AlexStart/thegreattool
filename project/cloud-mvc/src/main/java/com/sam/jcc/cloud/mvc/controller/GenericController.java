/**
 * 
 */
package com.sam.jcc.cloud.mvc.controller;

import java.util.List;

import org.springframework.ui.ModelMap;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author Alec Kotovich
 * 
 */
public abstract class GenericController<T> implements ICRUD<T> {

	protected IService<T> serviceDelegate;

	public GenericController(IService<T> serviceDelegate) {
		this.serviceDelegate = serviceDelegate;
	}
	
	protected abstract String view();

	protected abstract T emptyFormMetadata();


	@Override
	public T create(T model) {
		return serviceDelegate.create(model);
	}

	@Override
	public T read(T model) {
		return serviceDelegate.read(model);
	}

	@Override
	public T update(T model) {
		return serviceDelegate.update(model);
	}

	@Override
	public void delete(T model) {
		serviceDelegate.delete(model);
	}

	@Override
	public List<? super T> findAll() {
		return serviceDelegate.findAll();
	}
	
	public String post(ModelMap model, T form) {
		T created = serviceDelegate.create(form);
		if (created == null) {
			created = emptyFormMetadata();
		}
		model.addAttribute("model", created);
		initView(model);
		return view();
	}

	public String get(ModelMap model) {
		model.addAttribute("model", emptyFormMetadata());
		initView(model);
		return view();
	}

	public String getPage(ModelMap model, Integer pageNumber) {
		model.addAttribute("model", emptyFormMetadata());
		initView(model);
		return view();
	}

	public String put(ModelMap model, T form) {
		T updated = serviceDelegate.update(form);
		if (updated == null) {
			updated = emptyFormMetadata();
		}
		model.addAttribute("model", updated);
		initView(model);
		return view();
	}

	public String delete(ModelMap model, T entry) {
		serviceDelegate.delete(entry);
		model.addAttribute("model", emptyFormMetadata());
		initView(model);
		return view();
	}
	
	public void findAndDelete(T entry) {
		serviceDelegate.findAndDelete(entry);
	}	

	protected List<? super T> initView(ModelMap model) {
		List<? super T> models = findAll();
		model.addAttribute("models", models);
		return models;
	}

	
}
