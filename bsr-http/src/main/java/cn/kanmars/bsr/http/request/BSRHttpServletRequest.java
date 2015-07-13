package cn.kanmars.bsr.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import cn.kanmars.bsr.http.context.BSRServletContext;

/**
 * BSR服务器的HttpServletRequest
 * @author baolong
 *
 */
public class BSRHttpServletRequest  implements HttpServletRequest {

	/**
	 * 请求属性表
	 */
	public final Map<String, Object> attributes = new HashMap<String, Object>();
	
	/**
	 * 获取某个属性
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	/**
	 * 获取属性迭代器
	 */
	public Enumeration<String> getAttributeNames() {
		return new Enumeration<String>() {
			Iterator<String> attributes_iterator = attributes.keySet().iterator();
			public boolean hasMoreElements() {
				return attributes_iterator.hasNext();
			}
			public String nextElement() {
				return attributes_iterator.next();
			}
			
		};
	}

	/**
	 * 字符集
	 */
	private String characterEncoding = null;
	
	/**
	 * 获取字符集
	 */
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/**
	 * 设置字符集
	 */
	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
		characterEncoding = env;
	}

	/**
	 * 报文长度
	 */
	private int contentLength = 0; 
	
	/**
	 * 获取报文长度
	 */
	public int getContentLength() {
		return contentLength;
	}
	
	/**
	 * 设置报文长度
	 * @param contentLength
	 */
	public void setContentLength(int contentLength){
		this.contentLength = contentLength;
	}

	/**
	 * contentType
	 */
	private String contentType = null;
	
	/**
	 * 获取contentType
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * 设置contentType
	 * @param contentType
	 */
	public void setContentType(String contentType){
		this.contentType = contentType;
	}

	/**
	 * 输入流
	 */
	private ServletInputStream inputStream = null;
	
	/**
	 * 获取InputStream
	 */
	public ServletInputStream getInputStream() throws IOException {
		return inputStream;
	}
	
	/**
	 * 设置inputStream
	 * @param inputStream
	 */
	public void setInputStream(ServletInputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * 参数列表
	 */
	private Map<String, String[]> parameterMap = new HashMap<String, String[]>();
	
	/**
	 * 获取参数
	 */
	public String getParameter(String name) {
		String[] value = parameterMap.get(name);
		if(value ==null){
			return null;
		}
		if(value.length==1){
			return value[0];
		}
		if(value.length>1){
			String result = "";
			for(int i=0;i<value.length;i++){
				if(i!=0){
					result +=",";
				}
				result += value[i];
			}
			return result;
		}
		return null;
	}

	/**
	 * 获取参数的名称
	 */
	public Enumeration<String> getParameterNames() {
		return new Enumeration<String>() {
			Iterator<String> parameter_iterator = parameterMap.keySet().iterator();
			public boolean hasMoreElements() {
				return parameter_iterator.hasNext();
			}
			public String nextElement() {
				return parameter_iterator.next();
			}
			
		};
	}

	/**
	 * 获取参数的值的数组
	 */
	public String[] getParameterValues(String name) {
		return parameterMap.get(name);
	}

	/**
	 * 获取参数的Map
	 */
	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}

	private String protocol = null;
	
	/**
	 * 返回协议
	 */
	public String getProtocol() {
		return protocol;
	}
	/**
	 * 设置协议名称
	 * @param protocol
	 */
	public void setProtocol(String protocol){
		this.protocol = protocol;
	}

	/**
	 * 未知参数，目前永久返回为空
	 * //TODO 将来进行弥补
	 */
	public String scheme = null;
	
	/**
	 * 未知参数，目前永久返回为空
	 * //TODO 将来进行弥补
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * 未知参数，目前永久返回为空
	 * //TODO 将来进行弥补
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	/**
	 * 服务器域名
	 */
	public String serverName = null;
	
	/**
	 * 服务器域名
	 */
	public String getServerName() {
		return serverName;
	}
	
	/**
	 * 设置服务器域名
	 */
	public void setServerName(String serverName){
		this.serverName = serverName;
	}
	
	private int serverPort = 0;

	/**
	 * 获取服务器端口
	 */
	public int getServerPort() {
		return serverPort;
	}
	
	/**
	 * 设置一个服务器端口
	 * @param serverPort
	 */
	public void setServerPort(int serverPort){
		this.serverPort = serverPort;
	}

	/**
	 * 获取阅读器
	 */
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	/**
	 * 远程地址-IP形式
	 */
	private String remoteAddr = null;
	/**
	 * 获取远程IP
	 */
	public String getRemoteAddr() {
		return remoteAddr;
	}
	
	/**
	 * 设置远程地址
	 */
	public void setRemoteAddr(String remoteAddr){
		this.remoteAddr = remoteAddr;
	}


	/**
	 * 远程HOST
	 */
	private String remoteHost = null;
	
	/**
	 * 获取远程host
	 */
	public String getRemoteHost() {
		return remoteHost;
	}
	
	/**
	 * 设置远程host
	 * @param remoteHost
	 */
	public void setRemoteHost(String remoteHost){
		this.remoteHost = remoteHost;
	}

	/**
	 * 设置属性
	 */
	public void setAttribute(String name, Object o) {
		attributes.put(name, o);
	}

	/**
	 * 移除属性
	 */
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	/**
	 * 国际化信息
	 */
	private final List<Locale> locales = new ArrayList<Locale>();
	
	/**
	 * 国际化
	 */
	public Locale getLocale() {
		if(locales.size()>1)return locales.get(0);
		return null;
	}

	/**
	 * 国际化
	 */
	public Enumeration<Locale> getLocales() {
		return new Enumeration<Locale>() {
			Iterator<Locale> ite = locales.iterator();
			public boolean hasMoreElements() {
				return ite.hasNext();
			}
			public Locale nextElement() {
				return ite.next();
			}
		};
	}

	/**
	 * 是否加密链接
	 */
	public boolean secure = false;
	
	/**
	 * 是否是加密链接
	 */
	public boolean isSecure() {
		return secure;
	}
	
	/**
	 * 设置加密信息
	 * @param secure
	 */
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	/**
	 * 待实现
	 * //TODO 
	 */
	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	
	/**
	 * 获得某文件的真实路径
	 */
	@Deprecated
	public String getRealPath(String path) {
		if(path.startsWith("/")){
			return path;
		}else{
			return "/"+path;
		}
	}

	/**
	 * 远程服务端口
	 */
	private int remotePort = 0;
	
	/**
	 * 获取远程服务端口
	 */
	public int getRemotePort() {
		return remotePort;
	}
	
	/**
	 * 设置远程端口
	 * @param remotePort
	 */
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	private String localName = null;
	
	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	/**
	 * 本地地址
	 */
	private String localAddr = null;
	
	/**
	 * 设置本地地址
	 * @param localAddr
	 */
	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	/**
	 * 获取本地地址
	 */
	public String getLocalAddr() {
		return localAddr;
	}

	/**
	 * 本地端口
	 */
	private int localPort = 0;
	/**
	 * 本地端口
	 */
	public int getLocalPort() {
		return localPort;
	}
	/**
	 * 本地端口
	 */
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	/**
	 * 获取servletContext上下文
	 */
	public ServletContext getServletContext() {
		return BSRServletContext.getBSRServletContext();
	}

	/**
	 * //TODO servlet3.0接口规范
	 */
	public AsyncContext startAsync() throws IllegalStateException {
		return null;
	}

	/**
	 * //TODO servlet3.0接口规范
	 */
	public AsyncContext startAsync(ServletRequest servletRequest,
			ServletResponse servletResponse) throws IllegalStateException {
		return null;
	}

	/**
	 * //TODO servlet3.0接口规范
	 */
	public boolean isAsyncStarted() {
		return false;
	}

	/**
	 * 是否支持servlet3.0接口规范async
	 * //TODO servlet3.0接口规范
	 */
	public boolean isAsyncSupported() {
		return false;
	}
	
	/**
	 * //TODO servlet3.0接口规范
	 */
	public AsyncContext getAsyncContext() {
		
		return null;
	}

	/**
	 * //TODO 未知内容
	 */
	public DispatcherType getDispatcherType() {

		return null;
	}

	/**
	 * 返回身份验证方法的名称，basic，SSL，form，client_cert和digest，若没有身份验证则为null
	 * //TODO 待实现
	 */
	public String getAuthType() {
		return null;
	}

	private Cookie[] cookies = null;
	
	/**
	 * cookies
	 */
	public Cookie[] getCookies() {
		return cookies;
	}
	/**
	 * 设置cookies
	 * @param cookies
	 */
	public void setCookies(Cookie[] cookies) {
		this.cookies = cookies;
	}

	/**
	 * 用于将指定头信息的部分转换成方便转换为时间类型的长整数型，简化getHeaders
	 */
	public long getDateHeader(String name) {
		return Long.parseLong(getHeader(name));
	}

	/**
	 * 头部信息区域
	 */
	private Map<String,String> headers = new HashMap<String, String>();
	
	/**
	 * 获取一个头部
	 */
	public String getHeader(String name) {
		return null;
	}
	
	/**
	 * 获取头部信息Map
	 * @return
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * 设置头部信息
	 * @param headers
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * 获取Header的迭代器
	 */
	public Enumeration<String> getHeaders(String name) {
		String headervalue = getHeader(name);
		//多个标签用逗号分隔
		String[] headers = headervalue.trim().split(",");
		List<String> headers_list = new ArrayList<String>();
		for(String header : headers){
			headers_list.add(header);
		}
		final Iterator<String> headers_list_iterators =  headers_list.iterator();
		return new Enumeration<String>() {
			public boolean hasMoreElements() {
				return headers_list_iterators.hasNext();
			}
			public String nextElement() {
				return headers_list_iterators.next();
			}
		};
	}

	/**
	 * 获取header的names
	 */
	public Enumeration<String> getHeaderNames() {
		return new Enumeration<String>() {
			Iterator<String> headers_iterators = headers.keySet().iterator();
			public boolean hasMoreElements() {
				return headers_iterators.hasNext();
			}
			public String nextElement() {
				return headers_iterators.next();
			}
		};
	}

	/**
	 * 获取Int类型的Header
	 */
	public int getIntHeader(String name) {
		return Integer.parseInt(getHeader(name));
	}

	/**
	 * 请求方法
	 */
	private String method=null;
	/**
	 * 请求方法
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * 请求方法
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * 获取服务器信息
	 */
	private String pathInfo = null;

	/**
	 * 获取服务器信息
	 */
	public String getPathInfo() {
		return pathInfo;
	}

	/**
	 * 获取服务器信息
	 */
	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRequestURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpSession getSession(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean authenticate(HttpServletResponse response)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	public void login(String username, String password) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	public Part getPart(String name) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

}
