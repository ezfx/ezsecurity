package io.github.ezfx.ezsecurity.impl.web.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

import io.github.ezfx.ezsecurity.core.request.WebRequestContext;
import io.github.ezfx.ezsecurity.core.user.LoginModule;

/**
 * 一个登陆模块的默认实现
 * @author wangjg
 *
 */
public abstract class AbstractCacheLoginModule implements LoginModule{
	private String sessionCookieName;
	
	public AbstractCacheLoginModule() {
	}

	public Object login() {
		HttpServletRequest request = WebRequestContext.getRequest();
		Cookie cookie = WebUtils.getCookie(request, sessionCookieName);
		String sessionId = cookie.getValue();
		Object user = this.getCachedUser(sessionId);
		return user;
	}
	
	protected abstract Object getCachedUser(String sessionId);

	public String getSessionCookieName() {
		return sessionCookieName;
	}

	public void setSessionCookieName(String userSessionKey) {
		this.sessionCookieName = userSessionKey;
	}

}
