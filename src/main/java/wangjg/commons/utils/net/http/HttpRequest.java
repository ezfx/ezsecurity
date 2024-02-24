package wangjg.commons.utils.net.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author wangjg
 *
 */
public class HttpRequest {

	private String url;
	private Map<String,String> headers;
	private Map<String,String> params;
	private byte[] postBody;
	private Map<String, File> fileParams;
	private String defaultCharset = "iso-8859-1";
	private int connectTimeout = 1000*10;
	private int readTimeout = 0;
	
	public HttpRequest() {
		CookieManager cm = new CookieManager();
		cm.getCookieStore().removeAll();
		CookieHandler.setDefault(cm);
	}

	private String boundary = "----wjghttpformboundary----";
	
	public HttpResult loadText() throws IOException {
		HttpResult result = execute();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = (InputStream) result.getContent();
		
		String encoding = result.getHeaders().get("Content-Encoding");
		if("gzip".equals(encoding)){
			in = new GZIPInputStream(in);
		}
		
		copyAndClose(in, out);
		result.setContent(null);
		
		String charset = getCharset(result.getHeaders().get("Content-Type"));
		if(charset==null){
			charset = this.defaultCharset;
		}
		
		String body = new String(out.toByteArray(), charset);
		result.setContent(body);
		
		return result;
	}
	
	public HttpResult loadFile(String filePath) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(filePath);
			HttpResult result = this.loadFile(out);
			return result;
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public HttpResult loadFile(OutputStream out) throws IOException {
		HttpResult result = this.execute();
		InputStream in = (InputStream) result.getContent();

		String encoding = result.getHeaders().get("Content-Encoding");
		if("gzip".equals(encoding)){
			in = new GZIPInputStream(in);
		}
		
		copyAndClose(in, out);
		result.setContent(null);
		return result;
	}
	
	public HttpResult execute() throws IOException {
		HttpURLConnection conn = getURLConnection();

		if(headers!=null && headers.size()>0){
			String host = headers.get("Host");
			if(host==null || host.length()==0){
				try {
					URI uri = new URI(this.url);
					host = uri.getHost();
					if(host!=null && host.length()>0){
						conn.setRequestProperty("Host", host);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			for(Entry<String, String> entry:headers.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		
		if(this.fileParams!=null && this.fileParams.size()>0){
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

	        OutputStream out = conn.getOutputStream();
	        DataOutputStream ds = new DataOutputStream(out);
	        
	        this.writeTextParts(ds, boundary);
			this.writeFileParts(ds, boundary);
			
	        ds.writeBytes("--" + boundary + "--" + "\r\n");
	        ds.writeBytes("\r\n");
	        
	        ds.flush();
		}else if(this.postBody!=null){
			conn.setRequestProperty("Content-Length", String.valueOf(postBody.length));
			OutputStream out = conn.getOutputStream();
			out.write(postBody);
			out.flush();
			out.close();
		}else{
			String queryString = getQueryString(this.params);
			if(queryString!=null){
				conn.setRequestMethod("POST");
				byte[] data = queryString.getBytes("utf-8");
				conn.setRequestProperty("Content-Length", String.valueOf(data.length));
				OutputStream out = conn.getOutputStream();
				out.write(data);
				out.flush();
				out.close();
			}
		}

		HttpResult result = getHttpResult(conn);
		return result;
	}
	
	private static String getQueryString(Map<String,String> params) throws UnsupportedEncodingException{
		if(params==null || params.size()==0){
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry:params.entrySet()){
			String key = entry.getKey();
			String val = entry.getValue();
			if(key==null||key.isEmpty()||val==null||val.isEmpty()){
				continue;
			}
			sb.append(key);
			sb.append('=');
			sb.append(URLEncoder.encode(val, "utf-8"));
			sb.append('&');
		}
		
		if(sb.length()>0){
			return sb.substring(0, sb.length()-1);
		}

		return sb.toString();
	}

    private void writeTextParts(DataOutputStream ds, String boundary) throws IOException {
    	if(this.params==null||this.params.isEmpty()){
    		return;
    	}
    	for(Entry<String, String> entry:params.entrySet()){
            String name = entry.getKey();
            String value = entry.getValue();
			if(name==null||name.isEmpty()||value==null||value.isEmpty()){
				continue;
			}
            ds.writeBytes("--" + boundary + "\r\n");
            ds.writeBytes("Content-Disposition: form-data; name=\"" + name+ "\"\r\n");
            ds.writeBytes("\r\n");
            ds.writeBytes(encode(value) + "\r\n");
        }
    }
    
    private void writeFileParts(DataOutputStream ds, String boundary) throws IOException {
    	if(this.fileParams==null||this.fileParams.isEmpty()){
    		return;
    	}
    	for(Entry<String, File> entry:fileParams.entrySet()){
    		String name = entry.getKey();
            File file = entry.getValue();
            ds.writeBytes("--" + boundary + "\r\n");
            ds.writeBytes("Content-Disposition: form-data; name=\"" + name
                    + "\"; filename=\"" + encode(file.getName()) + "\"\r\n");
            ds.writeBytes("Content-Type: application/octet-stream\r\n");
            ds.writeBytes("\r\n");
            
            InputStream in = new FileInputStream(file);
            copy(in, ds);
            ds.writeBytes("\r\n");  
        }  
    } 
    
    private String encode(String value) throws UnsupportedEncodingException{  
        return URLEncoder.encode(value, "UTF-8");  
    }  
	
	private HttpResult getHttpResult(HttpURLConnection conn) throws IOException{
		HttpResult result = new HttpResult();
		Map<String, List<String>> headers = conn.getHeaderFields();
//		_printHeaders(headers);
		Map<String, String> headerMap = new HashMap<String, String>();
		for(Entry<String, List<String>> entry:headers.entrySet()){
			if(StringUtils.isNotBlank(entry.getKey())){
				headerMap.put(entry.getKey(), entry.getValue().get(0));
			}
		}
		result.setHeaders(headerMap);
		result.setStatus(conn.getResponseCode());
		if(conn.getResponseCode()!=200){
			InputStream errorStream = conn.getErrorStream();
			if(errorStream!=null){
				result.setContent(errorStream);
			}else{
				result.setContent(conn.getInputStream());
			}
		}else{
			result.setContent(conn.getInputStream());
		}
		
		return result;
	}
	
	private void _printHeaders(Map<String, List<String>> headers){
		Map<String, String> headerMap = new HashMap<String, String>();
		for(Entry<String, List<String>> entry:headers.entrySet()){
			String name = entry.getKey();
			List<String> values = entry.getValue();
			for(String val:values){
				System.out.println(name+" : "+val);
			}
		}
		System.out.println();
	}
	
	private HttpURLConnection getURLConnection() throws IOException{
		URL url = new URL(this.url);
		String protocol = url.getProtocol();
		if("https".equals(protocol)){
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		
		return conn;
	}

    /** 
     * 初始化HttpsURLConnection. 
     * @param password 密码 
     * @param keyStorePath 密钥库路径 
     * @param trustStorePath 信任库路径 
     * @throws Exception 
     */  
	void initHttps(String password, String keyStorePath, String trustStorePath) throws Exception {
		SSLContext sslContext = null;
		
		try {
			sslContext = getSSLContext(password, keyStorePath, trustStorePath);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		
		HostnameVerifier hv = new HostnameVerifier(){
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

    /** 
     * 获得SSLSocketFactory. 
     * @param password 密码 
     * @param keyStorePath 密钥库路径 
     * @param trustStorePath 信任库路径 
     * @return SSLSocketFactory 
     * @throws Exception 
     */  
	public static SSLContext getSSLContext(String password, String keyStorePath, String trustStorePath) throws Exception {
		// 实例化密钥库
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		// 获得密钥库
		KeyStore keyStore = getKeyStore(password, keyStorePath);
		// 初始化密钥工厂
		keyManagerFactory.init(keyStore, password.toCharArray());

		// 实例化信任库
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		// 获得信任库
		KeyStore trustStore = getKeyStore(password, trustStorePath);
		// 初始化信任库
		trustManagerFactory.init(trustStore);
		// 实例化SSL上下文
		SSLContext ctx = SSLContext.getInstance("TLS");
		// 初始化SSL上下文
		ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
		// 获得SSLSocketFactory
		return ctx;
	}

	/**
	 * 获得KeyStore.
	 * @param keyStorePath 密钥库路径
	 * @param password 密码
	 * @return 密钥库
	 * @throws Exception
	 */
    private static KeyStore getKeyStore(String password, String keyStorePath)throws Exception {
        // 实例化密钥库  
        KeyStore ks = KeyStore.getInstance("JKS");  
        // 获得密钥库文件流  
        FileInputStream is = new FileInputStream(keyStorePath);  
        // 加载密钥库  
        ks.load(is, password.toCharArray());  
        // 关闭密钥库文件流  
        is.close();  
        return ks;  
    }
    
    private static String getCharset(String str){
		String charset = null;
		if(str!=null){
			int pos = str.indexOf("charset=");
			if(pos!=-1){
				charset = str.substring(pos+8);
			}
		}
		return charset;
	}
	
	static void copyAndClose(InputStream in, OutputStream out) throws IOException{
		try {
			copy(in, out);
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 在流之间拷贝数据
	 * @param in 输入流
	 * @param out 输出流
	 * @throws IOException 读写异常
	 */
	static void copy(InputStream in, OutputStream out) throws IOException{
		byte[] buf = new byte[512];
		int count = in.read(buf); 
		while(count!=-1){ 
			out.write(buf,0,count); 
			count = in.read(buf);
		}
	}
	
	public static void main(String args[]) throws Exception{
		String url = "http://1111.ip138.com/ic.asp";
		HttpRequest request = new HttpRequest();

		request.setConnectTimeout(1000*30);
		request.setUrl("http://www.rebeccaminkoff.com/media/catalog/product/cache/1/thumbnail/9df78eab33525d08d6e5fb8d27136e95/h/f/hf15enyr04_viktor_zipper_tote_1_black_a_lr.jpg");
		String savePath = "d:/temp/aaa.jpg";
		request.loadFile(savePath);
		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setFileParams(Map<String, File> fileParams) {
		this.fileParams = fileParams;
	}

	public void setDefaultCharset(String defaultCharset) {
		this.defaultCharset = defaultCharset;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public byte[] getPostBody() {
		return postBody;
	}

	public void setPostBody(byte[] postBody) {
		this.postBody = postBody;
	}
}
