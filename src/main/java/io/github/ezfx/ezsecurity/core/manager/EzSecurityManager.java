package io.github.ezfx.ezsecurity.core.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ezfx.ezsecurity.core.exception.SecurityException;
import io.github.ezfx.ezsecurity.core.user.LoginModule;

/**
 * 
 * @author wangjg
 *
 */
public class EzSecurityManager {
	private static final Logger logger = LoggerFactory.getLogger(EzSecurityManager.class);
	
	protected LoginModule loginModule;
	
	public void validate(Object request){
		Object user = loginModule.login();
		if(user==null){
			throw new SecurityException(SecurityException.NOLOGIN, "没有登陆");
		}
		boolean allow = this.validate(request, user);
		if(!allow){
			logger.info("没有权限,user={},request={}", user, request);
			throw new SecurityException(SecurityException.PERMISSION, "没有权限");
		}
	}
	
	protected boolean validate(final Object request, final Object user) {
		
		return true;
	}

	public LoginModule getLoginModule() {
		return loginModule;
	}
	
	public void setLoginModule(LoginModule loginModule) {
		this.loginModule = loginModule;
	}
}
