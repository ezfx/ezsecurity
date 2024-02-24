package io.github.ezfx.ezsecurity.impl.web.login;

import javax.servlet.http.HttpServletRequest;

import io.github.ezfx.ezsecurity.core.request.WebRequestContext;
import io.github.ezfx.ezsecurity.core.user.LoginModule;

/**
 * 一个登陆模块的默认实现
 * 
 * @author wangjg
 *
 */
public class JSessionLoginModule implements LoginModule {
	private String userSessionKey;

	public JSessionLoginModule() {
	}

	public Object login() {
		HttpServletRequest request = WebRequestContext.getRequest();
		Object user = request.getSession().getAttribute(userSessionKey);
		return user;
	}

	public Object logout() {
		HttpServletRequest request = WebRequestContext.getRequest();
		Object user = request.getSession().getAttribute(userSessionKey);
		request.getSession().removeAttribute(userSessionKey);
		return user;
	}

	public String getUserSessionKey() {
		return userSessionKey;
	}

	public void setUserSessionKey(String userSessionKey) {
		this.userSessionKey = userSessionKey;
	}

}
