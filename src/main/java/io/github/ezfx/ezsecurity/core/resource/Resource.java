package io.github.ezfx.ezsecurity.core.resource;

public interface Resource {

	public static final int TYPE_HTTP = 0;
	
	public static final int TYPE_EXEC = 1;
	
	public int getType();

	public String getPattern();
	
}
