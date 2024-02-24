package io.github.ezfx.ezsecurity.core.resource;

import java.util.Set;

import io.github.ezfx.ezsecurity.core.authority.AuthoritySet;

/**
 * http资源，可以是一个或一组url
 * @author wangjigang1
 *
 */
public class ExecutionResource implements AuthoritySet{
	private String className;
	private String methodName;
	
	private Set authorities;

	public ExecutionResource() {
		super();
	}
	
	public Set getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set authorities) {
		this.authorities = authorities;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

}
