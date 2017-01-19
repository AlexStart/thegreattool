/**
 * 
 */
package com.sam.jcc.cloud.mvc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sam.jcc.cloud.mvc.dto.HealthDTO;
import com.sam.jcc.cloud.mvc.service.HealthService;

/**
 * @author Alec Kotovich
 * 
 */
@Controller
@RequestMapping("/health")
public class HealthController {

	@Autowired
	private HealthService healthService;

	protected String view() {
		return "health";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(ModelMap model) {
		initView(model);
		return view();
	}

	private void initView(ModelMap model) {
		List<? super HealthDTO> dtos = healthService.findAll();
		model.addAttribute("models", dtos);
	}

}
