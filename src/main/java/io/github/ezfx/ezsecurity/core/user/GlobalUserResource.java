package io.github.ezfx.ezsecurity.core.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;

import io.github.ezfx.ezsecurity.core.resource.Resource;

public class GlobalUserResource {
	
	private  HashMap<String,Resource> global = new HashMap<String,Resource>();
	
	public synchronized Resource saveResource(Resource resource){
		Resource result = this.global.get(resource.getPattern());
		if(result==null){
			this.global.put(resource.getPattern(), resource);
			result = resource;
		}else{
			try {
				PropertyUtils.copyProperties(result, resource);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	public synchronized HashSet<Resource> saveUserResources(Set<Resource> resources){
		HashSet<Resource> result = new HashSet<Resource>();
		for(Resource resource: resources){
			Resource r = this.saveResource(resource);
			result.add(r);
		}
		return result;
	}

}
