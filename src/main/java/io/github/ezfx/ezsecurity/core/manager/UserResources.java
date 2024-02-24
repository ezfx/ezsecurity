package io.github.ezfx.ezsecurity.core.manager;

import java.util.List;
import java.util.Set;

import io.github.ezfx.ezsecurity.core.resource.Resource;

public interface UserResources {
	
	public Set<Resource> find(int type, boolean abs);
	
	public List<Resource> getList();

}
