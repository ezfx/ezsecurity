package io.github.ezfx.ezsecurity.core.resource;

/**
 * 
 * @author wangjg
 *
 */
@Deprecated
public interface ResourceMatcher {

	public boolean match(Object resource, Object request);

}