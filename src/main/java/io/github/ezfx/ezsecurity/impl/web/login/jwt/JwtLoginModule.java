package io.github.ezfx.ezsecurity.impl.web.login.jwt;

import javax.servlet.http.HttpServletRequest;

import io.github.ezfx.ezsecurity.core.request.WebRequestContext;
import io.github.ezfx.ezsecurity.core.user.LoginModule;

public class JwtLoginModule implements LoginModule {
	
	private Class userClass;

	public Object login() {
		JwtCodec jwtCodec = new JwtCodec();
		String token = this.getJwtToken();
		JwtPayload<Object> user = jwtCodec.verify(token , userClass);
		return user;
	}

	private String getJwtToken() {
		HttpServletRequest request = WebRequestContext.getRequest();
		String jwt = request.getParameter("jwt");
		if(jwt==null || jwt.isEmpty()){
			jwt = request.getHeader("jwt");
		}
		
		return jwt;
	}

}
