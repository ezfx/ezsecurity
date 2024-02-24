package io.github.ezfx.ezsecurity.impl.web.login.jwt;

import java.util.Date;

public class JwtPayload<T> {
	//jwt_id
	private String jti;
	//过期时间
	private Date exp;

	private T bizObj;

	public JwtPayload() {
	}

	public JwtPayload(T bizObj) {
		this.bizObj = bizObj;
	}

	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}

	public Date getExp() {
		return exp;
	}

	public void setExp(Date exp) {
		this.exp = exp;
	}

	public T getBizObj() {
		return bizObj;
	}

	public void setBizObj(T bizObj) {
		this.bizObj = bizObj;
	}
	
}
