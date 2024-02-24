package io.github.ezfx.ezsecurity.core.resource;

import io.github.ezfx.ezsecurity.core.request.ExecutionRequest;
import wangjg.commons.utils.regex.Wildcard;

public class ExecutionResourceMatcher implements ResourceMatcher {

	public boolean match(Object resource, Object request) {
		if(!(resource instanceof ExecutionResource)){
			return false;
		}
		if(!(request instanceof ExecutionRequest)){
			return false;
		}
		ExecutionResource res = (ExecutionResource)resource;
		ExecutionRequest req = (ExecutionRequest)request;
		if(res.getClassName()!=null && !Wildcard.matches(res.getClassName(), req.getTarget().getClass().getName())){
			return false;
		}
		if(res.getMethodName()!=null && !Wildcard.matches(res.getMethodName(), req.getMethod().getName())){
			return false;
		}
		return true;
	}
	
}
