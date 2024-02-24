package io.github.ezfx.ezsecurity.impl.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.ezfx.ezsecurity.core.request.WebRequestContext;
import io.github.ezfx.ezsecurity.impl.web.manager.WebSecurityManager;

public class SecurityFilter extends WebSecurityManager implements Filter {
	private static Log log = LogFactory.getLog(SecurityFilter.class);
	
	public void destroy() {
		log.debug("destroy.");
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		try{
			WebRequestContext.setRequest(request);

			this.validate(request);
			
			chain.doFilter(req, res);
			
		} finally {
			WebRequestContext.setRequest(null);
		}

	}

	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

}

/*
 web.xml配置说明
	<!-- security filter  -->
	<filter>
		<filter-name>SecurityFilter</filter-name>
		<filter-class>wangjg.security.interceptor.servlet.ServletFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>utf-8</param-value>
		</init-param>
	</filter>
	
	<filter-mapping>
		<filter-name>SecurityFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	
 */
