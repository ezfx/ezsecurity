package io.github.ezfx.ezsecurity.core.request;

public interface Request {
	
	public String getPath();
	
	public Object getParameter(Object key);

}
