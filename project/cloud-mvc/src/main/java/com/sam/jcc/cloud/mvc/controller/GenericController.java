/**
 * 
 */
package com.sam.jcc.cloud.mvc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.ui.ModelMap;

import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author Alec Kotovich
 * 
 */
public abstract class GenericController<T, I> {

	private IService<I> serviceDelegate;

	protected ConversionService conversionService;

	public GenericController(IService<I> serviceDelegate, ConversionService conversionService) {
		this.serviceDelegate = serviceDelegate;
		this.conversionService = conversionService;
	}

	protected abstract String view();

	protected abstract T emptyFormMetadata();

	protected abstract Map<String, String> convertDTO(T form);

	protected abstract T convertModel(I model);
	
	protected abstract List<? super T> convertModels(List<? super I> models);
	
	private I create(Map<String, String> i) {
		return serviceDelegate.create(i);
	}

	private I read(I model) {
		return serviceDelegate.read(model);
	}

	private I update(Map<String, String> i) {
		return serviceDelegate.update(i);
	}

	private void delete(Map<String, String> i) {
		serviceDelegate.delete(i);
	}

	private List<? super I> findAll() {
		return serviceDelegate.findAll();
	}

	public String post(ModelMap model, T form) {
		Map<String, String> i = convertDTO(form);
		I created = create(i);
		T resultDTO = null;
		if (created == null) {
			resultDTO = emptyFormMetadata();
		} else {
			resultDTO = convertModel(created);
		}
		model.addAttribute("model", resultDTO);
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
		Map<String, String> i = convertDTO(form);
		I updated = update(i);
		T resultDTO = null;
		if (updated == null) {
			resultDTO = emptyFormMetadata();
		} else {
			resultDTO = convertModel(updated);
		}
		model.addAttribute("model", resultDTO);
		initView(model);
		return view();
	}

	public String delete(ModelMap model, T entry) {
		Map<String, String> i = convertDTO(entry);
		delete(i);
		model.addAttribute("model", emptyFormMetadata());
		initView(model);
		return view();
	}

	public void findAndDelete(T entry) {
		Map<String, String> i = convertDTO(entry);
		serviceDelegate.findAndDelete(i);
	}

	protected List<? super T> initView(ModelMap model) {
		List<? super I> models = findAll();
		List<? super T> dtos = convertModels(models);
		model.addAttribute("models", dtos);
		return dtos;
	}
	
}
