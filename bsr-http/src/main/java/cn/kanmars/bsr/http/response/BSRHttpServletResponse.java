package cn.kanmars.bsr.http.response;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import cn.kanmars.bsr.http.util.StringUtils;

/**
 * BSR服务器的HttpServletResPonse
 * @author baolong
 *
 */
public class BSRHttpServletResponse implements HttpServletResponse {

	private String characterEncoding;
	
	public String getCharacterEncoding() {
		return characterEncoding;
	}
	
	public void setCharacterEncoding(String charset) {
		this.characterEncoding = charset;
	}

	private String contentType;
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String type) {
		this.contentType = type;
	}
	
	private ServletOutputStream servletOutputStream;

	public void setOutPutStream(ServletOutputStream servletOutputStream){
		this.servletOutputStream = servletOutputStream;
	}
	
	public ServletOutputStream getOutputStream() throws IOException {
		return servletOutputStream;
	}

	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(servletOutputStream);
	}
	
	private int contentLength;

	public int getContentLength(){
		return contentLength;
	}
	public void setContentLength(int len) {
		contentLength = len;
	}
	

	private int buffserSize;
	

	public void setBufferSize(int size) {
		this.buffserSize = size;
	}

	public int getBufferSize() {
		return buffserSize;
	}

	public void flushBuffer() throws IOException {
		
	}

	public void resetBuffer() {

	}

	/**
	 * 流是否关闭，默认未关闭
	 */
	public boolean isCommitted() {
		return false;
	}

	public void reset() {
		
	}

	private Locale locale = null;
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public List<Cookie> cookies=new ArrayList<Cookie>();

	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	public boolean containsHeader(String name) {
		
		return false;
	}

	public String encodeURL(String url) {
		
		return URLEncoder.encode(url);
	}

	public String encodeRedirectURL(String url) {
		return URLEncoder.encode(url);
	}

	public String encodeUrl(String url) {
		return URLEncoder.encode(url);
	}

	public String encodeRedirectUrl(String url) {
		return URLEncoder.encode(url);
	}

	
	/**
	 * 头部信息区域
	 */
	private Map<String,String> headers = new HashMap<String, String>();

	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	public void setIntHeader(String name, int value) {
		headers.put(name, ""+value);
	}

	public void addIntHeader(String name, int value) {
		headers.put(name, ""+value);
	}
	
	public void setDateHeader(String name, long date) {
		headers.put(name, ""+date);
	}

	public void addDateHeader(String name, long date) {
		headers.put(name, ""+date);
	}
	
	/**
	 * 获取一个头部
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	/**
	 * 获取头部信息Map
	 * @return
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}
	
	/**
	 * 获取某一个header的集合
	 */
	public Collection<String> getHeaders(String name) {
		String header_str = headers.get(name);
		List<String> header = new ArrayList<String>();
		for(String s : header_str.split(";")){
			if(StringUtils.isNotEmpty(s)){
				header.add(s);
			}
		}
		return header;
	}

	private int status_code;
	
	private String status_message;
	
	public void setStatus(int sc) {
		this.status_code = sc;
		this.status_message = "";
	}

	public void setStatus(int sc, String sm) {
		this.status_code = sc;
		this.status_message = sm;
	}

	public int getStatus() {
		return status_code;
	}
	
	public void sendError(int sc, String msg) throws IOException {
		this.status_code = sc;
		this.status_message = msg;
	}

	public void sendError(int sc) throws IOException {
		this.status_code = sc;
		this.status_message = "";
		
	}

	private String redirectLocation;
	
	public void sendRedirect(String location) throws IOException {
		redirectLocation = location;
	}
	

}
