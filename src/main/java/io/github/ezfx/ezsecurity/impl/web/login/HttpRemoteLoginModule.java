package io.github.ezfx.ezsecurity.impl.web.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ezfx.ezsecurity.core.request.WebRequestContext;
import io.github.ezfx.ezsecurity.core.user.LoginModule;
import wangjg.commons.utils.json.JacksonUtil;
import wangjg.commons.utils.net.http.HttpRequest;
import wangjg.commons.utils.net.http.HttpResult;

/**
 * 一个登陆模块的默认实现
 * @author wangjg
 *
 */
public class HttpRemoteLoginModule implements LoginModule{
	private static final Logger logger = LoggerFactory.getLogger(HttpRemoteLoginModule.class);
	
	private HttpRequest httpClient = new HttpRequest();
	private JacksonUtil ju = new JacksonUtil();
	
	//远程登录url
	protected String url;
	
	//用户信息在json中的位置
	protected String userBeanPath = "data";
	//用信息类
	protected Class<?> userBeanClass;
	
	public HttpRemoteLoginModule() {
		ju.setDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public HttpRemoteLoginModule(String url, String beanPath, Class userBeanClass) {
		this();
		this.url = url;
		this.userBeanPath = beanPath;
		this.userBeanClass = userBeanClass;
	}

	public Object login() {
		String cookie = this.getCookieStr();

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("Cookie", cookie);
		httpClient.setUrl(url);
		httpClient.setHeaders(headers);
		HttpResult result = null;
		try {
			result = httpClient.loadText();
			if(result.getStatus()==200){
				String json = (String) result.getContent();
				Object user = json2user(json);
				return user;
			}else{
				logger.warn("远程登陆失败,response={}", result);
			}
		} catch (Exception e) {
			logger.error("远程登陆失败:response={}", result, e);
		}
		return null;
	}
	
	protected Object json2user(String json) throws IOException{
		Object bean = ju.jsonToObj(json, this.userBeanPath, this.userBeanClass);
		return bean;
	}
	
	private String getCookieStr(){
		HttpServletRequest request = WebRequestContext.getRequest();
		String cookie = request.getHeader("Cookie");
		return cookie;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserBeanClass(String userBeanClassName) {
		try {
			this.userBeanClass = Class.forName(userBeanClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void setUserBeanPath(String beanPath) {
		this.userBeanPath = beanPath;
	}

}
