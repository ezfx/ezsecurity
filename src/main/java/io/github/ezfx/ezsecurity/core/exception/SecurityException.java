package io.github.ezfx.ezsecurity.core.exception;

/**
 * 安全异常
 * @author wangjg
 *
 */
public class SecurityException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 登录异常
	 */
	public static final int NOLOGIN = 1;
	/**
	 * 权限异常
	 */
	public static final int PERMISSION = 2;

	private Integer code;

	public SecurityException(Integer code) {
		this.code = code;
	}

	public SecurityException(Integer code, String message) {
		super(message);
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}
	
//	public Throwable fillInStackTrace() {
//		return this;
//	}
	
}
