package io.github.ezfx.ezsecurity.core.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;

public class MyExpressionEvaluator {

	private Map<String,Object> map=new HashMap<String,Object>();
	
	private static Pattern pattern;
	
	private Pattern getPattern(){
		if(pattern==null)
		synchronized(this){
			if(pattern==null){
				String operator = "==|!=|>|>=|<|<=";
				String p="[a-z0-9\\.\\$_]+("+operator+")[a-z0-9\\.\\$_]+";
		        pattern = Pattern.compile(p);
			}
		}
        return pattern;
	}
	
	public void putVariable(String name,Object value){
		map.put(name, value);
	}
	/**
	 * resource (o) user
	 * @param expression
	 * @return
	 */
	public Object evaluate(String expression){
		Matcher matcher = this.getPattern().matcher(expression);
        boolean b=matcher.matches();
        if(b){
        	String operator=matcher.group(1);
        	int pos = matcher.start(1);
        	String re = expression.substring(0, pos);
        	String ue = expression.substring(pos+operator.length());
        	Object rv = getValue(re);
        	Object uv = getValue(ue);
        	if("==".equals(operator)){
        		return rv==null?uv==null:rv.equals(uv);
        	}else if("!=".equals(operator)){
        		return rv==null?uv!=null:!rv.equals(uv);
        	}else if(">".equals(operator)){
        		return rv==null?false:((Comparable)rv).compareTo(uv)>0;
        	}else if(">=".equals(operator)){
        		return rv==null?uv==null:((Comparable)rv).compareTo(uv)>=0;
        	}else if("<".equals(operator)){
        		return rv==null?false:((Comparable)rv).compareTo(uv)<0;
        	}else if("<=".equals(operator)){
        		return rv==null?uv==null:((Comparable)rv).compareTo(uv)<=0;
        	}
        }
		return Boolean.FALSE;
	}
	
	private String getValue(String expression){
		if(this.isLetter(expression)){
			return expression.substring(1,expression.length()-1);
		}
		if(this.isNumeric(expression)){
			return expression;
		}
		int pos=expression.indexOf('.');
		if(pos==-1){
			this.map.get(expression);
		}
		String objKey = expression.substring(0,pos);
		String objProperty = expression.substring(pos);
		Object obj = this.map.get(objKey);
		try {
			return BeanUtils.getProperty(obj,objProperty);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private static boolean isLetter(String expression){
		return Pattern.matches("^'.*'$", expression);
	}
	private static boolean isNumeric(String expression){
		return Pattern.matches("^-?\\d+(\\.\\d+)?$", expression);
	}
	public static void main(String args[]){
		System.out.println(isNumeric("-110.1234"));
	}
}
