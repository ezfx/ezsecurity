package io.github.ezfx.ezsecurity.core.authority;

import java.util.Set;

/**
 * 权限集合
 * 资源和用户都可以实现；资源实现是，用必须实现且包含对应的权限才能访问
 * @author wangjg
 */
public interface AuthoritySet {

	public Set getAuthorities();
}
