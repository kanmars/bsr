package cn.kanmars.bsr.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
import cn.kanmars.bsr.http.session.BSRHttpSession;
import cn.kanmars.bsr.http.session.BSRHttpSessionHolder;
import cn.kanmars.bsr.http.util.DateUtils;
import cn.kanmars.bsr.http.util.IDCreator;
import cn.kanmars.bsr.http.util.StringUtils;

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
	 * 设置一个请求参数
	 * @param name
	 * @param value
	 */
	public void putParameter(String name,String value){
		parameterMap.put(name, new String[]{value});
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

	public String scheme = null;
	
	public String getScheme() {
		return scheme;
	}

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
		try {
			return DateUtils.getDateFromGMTStr(getHeader(name)).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 头部信息区域
	 */
	private Map<String,String> headers = new HashMap<String, String>();
	
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
	 * 获取服务器相对路径信息
	 */
	public void setPathInfo(String pathInfo) {
		
		this.pathInfo = pathInfo;
	}
	
	public String getPathTranslated() {
		
		return null;
	}
	
	/**
	 * 上下文路径
	 */
	private String contextPath;

	/**
	 * 获取上下文路径
	 */
	public String getContextPath() {
		return contextPath;
	}

	/**
	 * 设置上下文路径
	 * @param contextPath
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	/**
	 * 查询字符串
	 */
	private String queryString;
	/**
	 * 获取查询字符串
	 */
	public String getQueryString() {
		return queryString;
	}
	/**
	 * 设置查询字符串
	 * @param queryString
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	private String remoteUser;
	
	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}
	public String getRemoteUser() {
		return remoteUser;
	}

	/**
	 * 用户权限验证
	 * //TODO 待开发
	 */
	public boolean isUserInRole(String role) {
		return false;
	}
	
	/**
	 * 用户权限验证
	 * //TODO 待开发
	 */
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	private String requestedSessionId;
	
	/**
	 * sessionId
	 */
	public String getRequestedSessionId() {
		String header_jessionid = getHeader("jessionid");
		if(StringUtils.isNotEmpty(header_jessionid)){
			return header_jessionid;
		}else{
			requestedSessionId = IDCreator.createId("BSR", 30);
			getHeaders().put("jessionid", requestedSessionId);
		}
		return getHeader("jessionid");
	}

	private String requestURI;
	
	public String getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	private String requestURL;
	
	public StringBuffer getRequestURL() {
		return new StringBuffer(requestURL);
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	private String servletPath;

	public String getServletPath() {
		return servletPath;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	/**
	 * session获取
	 * //TODO 待开发
	 */
	public HttpSession getSession(boolean create) {
		BSRHttpSession session = BSRHttpSessionHolder.getBSRHttpSession(getRequestedSessionId());
		if(create && session == null){
			session = new BSRHttpSession();
			BSRHttpSessionHolder.addABSRHttpSession(session);
		}
		return session;
	}

	public HttpSession getSession() {
		return getSession(true);
	}
	
	/**
	 * 未知参数，目前永久返回false
	 */
	public boolean isRequestedSessionIdValid() {
		return false;
	}

	/**
	 * 未知参数，目前永久返回false
	 */
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	/**
	 * 未知参数，目前永久返回false
	 */
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	/**
	 * 未知参数，目前永久返回false
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	/**
	 * 未知参数，目前永久返回false
	 */
	public boolean authenticate(HttpServletResponse response)
			throws IOException, ServletException {
		return false;
	}

	/**
	 * 未知参数，目前永久返回false
	 */
	public void login(String username, String password) throws ServletException {
		
	}

	/**
	 * 未知参数，目前永久返回false
	 */
	public void logout() throws ServletException {
		
	}

	Map<String, Part> partsMap = null;
	/**
	 * Servlet3.0参数
	 */
	public Collection<Part> getParts() throws IOException, ServletException {
		return partsMap.values();
	}
	
	/**
	 * Servlet3.0参数
	 */
	public Part getPart(String name) throws IOException, ServletException {
		return partsMap.get(name);
	}

}
