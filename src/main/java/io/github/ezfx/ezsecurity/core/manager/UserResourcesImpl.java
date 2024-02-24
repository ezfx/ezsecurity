package io.github.ezfx.ezsecurity.core.manager;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.ezfx.ezsecurity.core.resource.Resource;

public class UserResourcesImpl implements UserResources {
	private TypeRes[] ress = new TypeRes[3];
	private List<Resource> list;

	public UserResourcesImpl() {
	}

	public Set<Resource> find(int type, boolean abs) {
		if(this.ress==null){
			this.initRess();
		}
		TypeRes tr = ress[type];
		if(tr!=null){
			return abs?tr.abs:tr.wc;
		}
		return Collections.emptySet();
	}

	private void initRess() {
		for(Resource res: this.list){
			this.save(res);
		}
	}

	protected void save(Resource resource){
		TypeRes tr = ress[resource.getType()];
		if(tr==null){
			tr = new TypeRes();
			ress[resource.getType()] = tr;
		}
		if(resource.getPattern().indexOf('*')!=-1 || resource.getPattern().indexOf('?')!=-1){
			tr.wc.add(resource);
		}else{
			tr.abs.add(resource);
		}
	}

	public List<Resource> getList() {
		return list;
	}

	public void setList(List<Resource> list) {
		this.list = list;
		this.ress = null;
	}

}

class TypeRes{
	
	public HashSet abs = new HashSet();
	
	public HashSet wc = new HashSet();
	
}


