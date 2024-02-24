package io.github.ezfx.ezsecurity.core.validator;

/**
 * 
 * @author wangjg
 *
 */
public class ExpressionValidator {

	private String expression;

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public boolean validate(Object resource, Object user){
		MyExpressionEvaluator ev = new MyExpressionEvaluator();
		ev.putVariable("resource", resource);
		ev.putVariable("user", user);
		Boolean b=(Boolean) ev.evaluate(expression);
		return b;
	}

}
