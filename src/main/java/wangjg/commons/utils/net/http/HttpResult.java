/**
 * 
 */
package wangjg.commons.utils.net.http;

import java.util.Map;

/**
 * @author wangjg
 *
 */
public class HttpResult{
	private int status ;
	private Map<String,String> headers;
	private Object content;
	
	public String getContentType(){
		if(headers==null){
			return null;
		}
		return headers.get("Content-Type");
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "HttpResult [status=" + status + ", headers=" + headers + ", content=" + content + "]";
	}
}
