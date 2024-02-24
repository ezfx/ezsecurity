package io.github.ezfx.ezsecurity.impl.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import io.github.ezfx.ezsecurity.core.manager.EzSecurityManager;
import io.github.ezfx.ezsecurity.core.request.ExecutionRequest;

/**
 * 
 * @author wangjg
 *
 */
public class ExecutionInterceptor extends EzSecurityManager implements MethodInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(ExecutionInterceptor.class);

	public Object invoke(MethodInvocation invocation) throws Throwable {
		StopWatch watch = new StopWatch();
		try {
			watch.start();

			ExecutionRequest request = new ExecutionRequest(invocation.getThis(), invocation.getMethod().getDeclaringClass(), invocation.getMethod(), invocation.getArguments());
			super.validate(request);
			
			return invocation.proceed();
		} finally {
			watch.stop();
			if (logger.isInfoEnabled()) {
				logger.info(watch.toString());
			}
		}
	}
	
}
