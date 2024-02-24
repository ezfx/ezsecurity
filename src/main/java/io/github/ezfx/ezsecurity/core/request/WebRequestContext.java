package io.github.ezfx.ezsecurity.core.request;

import javax.servlet.http.HttpServletRequest;

public class WebRequestContext {
	private static ThreadLocal<HttpServletRequest> requestContext = new ThreadLocal<HttpServletRequest>();

	public static void setRequest(HttpServletRequest request){
		requestContext.set(request);
	}

	public static HttpServletRequest getRequest(){
		return (HttpServletRequest) requestContext.get();
	}

}
