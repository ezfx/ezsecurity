package io.github.ezfx.ezsecurity.impl.spring.aop;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;

import io.github.ezfx.ezsecurity.core.manager.EzSecurityManager;
import io.github.ezfx.ezsecurity.core.request.ExecutionRequest;

/**
 * 
 * @author wangjg
 *
 */
public class ExecutionAdvice extends EzSecurityManager implements MethodBeforeAdvice{
	
	public void before(Method method, Object[] args, Object target) throws Throwable {
		ExecutionRequest request = new ExecutionRequest(target, method.getDeclaringClass(), method, args);
		super.validate(request);
	}
	
}
