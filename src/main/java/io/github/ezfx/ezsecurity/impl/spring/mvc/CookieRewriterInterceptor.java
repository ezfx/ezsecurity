package io.github.ezfx.ezsecurity.impl.spring.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import io.github.ezfx.ezsecurity.impl.web.manager.WebSecurityManager;

/**
 * 把参数写入cookie
 * @author wangjg
 * 
 */
public class CookieRewriterInterceptor extends WebSecurityManager implements HandlerInterceptor{
	private static final Logger logger = LoggerFactory.getLogger(CookieRewriterInterceptor.class);
	
	public String cookieStr = ";XCK";

	public CookieRewriterInterceptor() {
		super();
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}
	
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		this.handleCookieRewrite(request, response);
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
	
	protected void handleCookieRewrite(HttpServletRequest request, HttpServletResponse response){
		String xcookie = request.getParameter(cookieStr);
		if(StringUtils.isNotBlank(xcookie)){
			response.addHeader("Set-Cookie", xcookie);
		}
	}

	public String getCookieStr() {
		return cookieStr;
	}

	public void setCookieStr(String cookieName) {
		this.cookieStr = cookieName;
	}
	
}
