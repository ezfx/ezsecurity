package io.github.ezfx.ezsecurity.impl.web.login.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * JSON Web Tokens
 * 
 * @author wangjg
 *
 */
public class JwtCodec {
    private static final Logger logger = LoggerFactory.getLogger(JwtCodec.class);
	
	private String secret = "wjg";
	
	public String create(JwtPayload<?> jwtPayload) throws Exception{
		Object obj = jwtPayload.getBizObj();
		Map<String, Object> bizMap;
		if(obj instanceof Map){
			bizMap = (Map)obj;
		}else{
			bizMap = PropertyUtils.describe(obj);
		}
		
		String token = this.create(bizMap, jwtPayload.getExp());
		return token;
	}
	
	public <T> JwtPayload<T> verify(String token, Class<T> clazz){
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
		DecodedJWT jwt = verifier.verify(token);
		Map<String, Claim> claims = jwt.getClaims();
		
		Map<String, Object> bizMap = new HashMap<String, Object>();
		Set<Entry<String, Claim>> entrySet = claims.entrySet();
		for(Entry<String, Claim> entry:entrySet){
			Claim claim = entry.getValue();
			bizMap.put(entry.getKey(), this.asObject(claim));
		}
		
		try {
			T obj = clazz.newInstance();
			PropertyUtils.copyProperties(obj, bizMap);
			JwtPayload<T> payload = new JwtPayload<T>(obj);
			payload.setExp(jwt.getExpiresAt());
			return payload;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	String create(Map<String, Object> bizMap, Date expiresAt) throws Exception{
		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("alg", "HS256");
		headerMap.put("typ", "JWT");
		Builder builder = JWT.create().withHeader(headerMap);
		
		builder.withExpiresAt(expiresAt);
		
		Set<Entry<String, Object>> entrySet = bizMap.entrySet();
		for(Entry<String, Object> entry:entrySet){
			String name = entry.getKey();
			Object val = entry.getValue();
			if(val!=null && !"class".equals(name)){
				String valStr = ""+val;
				builder.withClaim(name, valStr);	
			}
		}
		
		String token = builder.sign(Algorithm.HMAC256(secret));
		
		return token;
	}
	
	public Map<String, Object> verify(String token) throws Exception{
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
		DecodedJWT jwt = null;
		try{
			jwt = verifier.verify(token);
		}catch(Exception e){
			logger.error("token解析失败:{}", e.getMessage() ,e);
			throw e;
		}
		Map<String, Claim> claims = jwt.getClaims();
		
		Map<String, Object> bizMap = new HashMap<String, Object>();
		
		Set<Entry<String, Claim>> entrySet = claims.entrySet();
		for(Entry<String, Claim> entry:entrySet){
			String name = entry.getKey();
			Claim claim = entry.getValue();
			Object value = this.asObject(claim);
			bizMap.put(name, value);
		}
		
		return bizMap;
	}
	
	private Object asObject(Claim claim){
		Object val = null;
		if(val==null){
			val = claim.asString();
		}
		if(val==null){
			val = claim.asLong();
		}
		if(val==null){
			val = claim.asInt();
		}
		if(val==null){
			val = claim.asDouble();
		}
		if(val==null){
			val = claim.asDate();
		}
		if(val==null){
			val = claim.asBoolean();
		}
		if(val==null){
			val = claim.asMap();
		}
		if(val==null){
			val = claim.asList(String.class);
		}
		if(val==null){
			val = claim.asArray(String.class);
		}
		
		return val;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public static String createToken(String secret) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");
		String token = JWT.create()
				.withHeader(map)//header
				.withClaim("name", "zwz")//payload
				.withClaim("age", "18")
//				.withClaim("persssssss", "admin:promotion, admin:order, admin:product, admin:goodsSupply, admin:supplyCooperType, admin:coupon, admin:product, admin:markShareConfig, admin:area, admin:productCategory, admin:tag, admin:refunds, admin:shippingMethod, admin:specification, admin:brand, admin:afterSales, admin:freight")
				.sign(Algorithm.HMAC256(secret));//加密
		return token;
	}
	
	public static void verifyToken(String token, String secret) throws Exception {
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
		DecodedJWT jwt = verifier.verify(token);
		Map<String, Claim> claims = jwt.getClaims();
		System.out.println(claims.get("name").asString());
		System.out.println(claims.get("age").asString());
	}

	public static void main(String[] args) throws Exception {
		String secret = "a";
		String token = JwtCodec.createToken(secret);
		System.out.println(token);
		JwtCodec.verifyToken(token, secret);
	}

}
