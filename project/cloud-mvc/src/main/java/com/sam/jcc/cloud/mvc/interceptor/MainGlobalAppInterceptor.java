/**
 * 
 */
package com.sam.jcc.cloud.mvc.interceptor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author Alec Kotovich
 * 
 */
public class MainGlobalAppInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		Locale locale = RequestContextUtils.getLocale(request);
		if (locale != null) {
			request.setAttribute("curr_lang", locale.toString());
		}
		super.postHandle(request, response, handler, modelAndView);
	}

}
