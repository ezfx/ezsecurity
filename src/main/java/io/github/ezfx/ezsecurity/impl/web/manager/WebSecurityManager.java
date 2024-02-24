package io.github.ezfx.ezsecurity.impl.web.manager;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ezfx.ezsecurity.core.manager.EzSecurityManager;
import io.github.ezfx.ezsecurity.core.manager.UserResources;
import io.github.ezfx.ezsecurity.core.manager.UserResourcesFactory;
import io.github.ezfx.ezsecurity.core.resource.Resource;
import wangjg.commons.utils.regex.Wildcard;

/**
 * 适合web项目的基类
 * @author wangjg
 * 
 */
public class WebSecurityManager extends EzSecurityManager {
	private static final Logger logger = LoggerFactory.getLogger(WebSecurityManager.class);
	
	public WebSecurityManager() {
	}

	protected boolean validate(final Object request, final Object user) {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		String path = this.getRequestPath(httpRequest);
		UserResourcesFactory urf = (UserResourcesFactory)user;
		UserResources urs = urf.get();
		Set<Resource> absResources = urs.find(Resource.TYPE_HTTP, true);
		boolean have = absResources.contains(path);
		if(have){
			return true;
		}
		Set<Resource> wildcardResources = ((UserResources)user).find(Resource.TYPE_HTTP, false);
		for(Object res1: wildcardResources){
			String pattern = res1.toString();
			if(Wildcard.matches(pattern, path)){
				return true;
			}
		}
		return false;
	}
	
	private String getRequestPath(HttpServletRequest request){
		String servletPath = request.getServletPath();
		String url = servletPath;
		url = url.replace("//", "/");
		return url;
	}
	
}
