package io.github.ezfx.ezsecurity.core.request;

import java.lang.reflect.Method;

/**
 * 表示一个对象方法请求
 * (未实现)
 * @author wangjg
 *
 */
public class ExecutionRequest implements Request{
	private Object target;
	private Class targetClass;
	private Method method;
	private Object[] arguments;
	
	public ExecutionRequest() {
	}

	public ExecutionRequest(Object target, Class targetClass, Method method, Object[] arguments) {
		super();
		this.target = target;
		this.targetClass = targetClass;
		this.method = method;
		this.arguments = arguments;
	}

	public Object getTarget() {
		return target;
	}
	
	public void setTarget(Object obj) {
		this.target = obj;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public void setMethod(Method method) {
		this.method = method;
	}
	
	public Object[] getArguments() {
		return arguments;
	}
	
	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Class getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class targetClass) {
		this.targetClass = targetClass;
	}

	public String getPath() {
		return this.targetClass.getName()+"."+this.method.getName();
	}

	public Object getParameter(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
