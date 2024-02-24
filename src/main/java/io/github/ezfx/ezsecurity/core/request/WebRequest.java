package io.github.ezfx.ezsecurity.core.request;

import java.util.Map;

/**
 * 标示一个HTTP请求
 * @author wangjg
 *
 */
public class WebRequest implements Request{
	private String path;
	private Map<Object, Object> parameters;

	public WebRequest() {
		super();
	}

	public WebRequest(String url) {
		this.path = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String url) {
		this.path = url;
	}
	
	public Object getParameter(Object key){
		Object params=this.parameters.get(key);
		if(params==null){
			return null;
		}
		if(!params.getClass().isArray()){
			return params.toString();
		}
		Object[] objs = (Object[])params;
		if(objs.length==1){
			return objs[0].toString();
		}else{
			return objs.toString();
		}
	}

	@Override
	public String toString() {
		return "HttpRequest [path=" + path + ", parameters=" + parameters + "]";
	}
	
}
