package io.github.ezfx.ezsecurity.impl.spring.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import io.github.ezfx.ezsecurity.core.request.WebRequestContext;
import io.github.ezfx.ezsecurity.impl.web.manager.WebSecurityManager;

/**
 * springmvc拦截器
 * @author wangjg
 * 
 */
public class EzsMvcInterceptor extends WebSecurityManager implements HandlerInterceptor{
	private static final Logger logger = LoggerFactory.getLogger(EzsMvcInterceptor.class);

	public EzsMvcInterceptor() {
		super();
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try{
			WebRequestContext.setRequest(request);
			
			this.validate(request);

			return true;
		} finally {
			WebRequestContext.setRequest(null);
		}
	}
	
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		WebRequestContext.setRequest(null);
	}
	
}
