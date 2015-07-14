package cn.kanmars.bsr.http.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import cn.kanmars.bsr.http.context.BSRServletContext;
import cn.kanmars.bsr.http.stream.BSRServletInputStream;
import cn.kanmars.bsr.http.util.ByteUtils;
import cn.kanmars.bsr.http.util.StringUtils;

public class BSRHttpServletRequestParser{
	
	public static final String default_characterencoding = "GBK";
	
	/**
	 * 判断是否已经收到了全部的报文
	 * @param bytes
	 * @return
	 */
	public static boolean isReceivedAll(ByteArrayOutputStream bao) throws Exception{
		
		byte[] bytes = bao.toByteArray();
		
		//对报文进行切割
		//获取报文的头部信息
		int endofreqline = ByteUtils.byteIndexOf(bytes, new byte[]{'\r','\n'}, 0);
		int endofhead = ByteUtils.byteIndexOf(bytes, new byte[]{'\r','\n','\r','\n'}, 0);
		if(endofhead < 0){
			return false;
		}
		HashMap<String,String> headers = new HashMap<String, String>();
		
		for(int i=endofreqline+2;i<endofhead;){
			int next_rn = ByteUtils.byteIndexOf(bytes, new byte[]{'\r','\n'}, i);
			if(next_rn == -1)break;
			if(next_rn-i == 0){
				i = next_rn+2;
				continue;
			}
			String headerStr = new String(bytes,i,next_rn-i).trim();
			if(headerStr.indexOf(":")>0){
				String[] header_arrays = headerStr.split(":");
				headers.put(header_arrays[0], header_arrays[1].trim());
			}
			i = next_rn+2;
		}
		
		//System.out.println(header);
		
		String context_length = headers.get("Content-Length");
		String transfer_encoding = headers.get("Transfer-Encoding");
		if(context_length!=null){
			if(bytes.length - endofhead - 4 == Integer.parseInt(context_length)){
				//长度-头部起始位置-头部分隔符 = 报文长度
				return true;
			}else if(bytes.length - endofhead - 4 < Integer.parseInt(context_length)){
				return true;
			}else{
				//throw new Exception("报文长度超出，主动断开");
				return true;
			}
		}else if(transfer_encoding!=null && transfer_encoding.equals("chunked")){
			if(
					bytes[bytes.length-7]=='\r'
					&&
					bytes[bytes.length-6]=='\n'
					&&
					bytes[bytes.length-5]=='0'
					&&
					bytes[bytes.length-4]=='\r'
					&&
					bytes[bytes.length-3]=='\n'
					&&
					bytes[bytes.length-2]=='\r'
					&&
					bytes[bytes.length-1]=='\n'
			)
			{
				return true;
			}else{
				return false;
			}
		}else {
			if(endofhead>0){
				return true;
			}else{
				return false;
			}
		}
	}
	
	/**
	 * 该方法的运行的前提是isReceivedAll返回true，即报文全部接受
	 * @param bao
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static HttpServletRequest parse(ByteArrayOutputStream bao) throws UnsupportedEncodingException{
		BSRHttpServletRequest bsrHttpServletRequest = new BSRHttpServletRequest();
		//报文全体信息
		byte[] bytes = bao.toByteArray();
		//解析请求航信息
		int endofreqline = ByteUtils.byteIndexOf(bytes, new byte[]{'\r','\n'}, 0);
		String reqLine = new String (bytes,0,endofreqline);
		String[] reqLine_spil = reqLine.split(" ");
		String method = reqLine_spil[0];
		String requestURI = reqLine_spil[1];
		String protocol = reqLine_spil[2];
		bsrHttpServletRequest.setMethod(method);
		bsrHttpServletRequest.setRequestURI(requestURI);
		bsrHttpServletRequest.setProtocol(protocol);
		bsrHttpServletRequest.setScheme("http");
		
		//解析头部信息
		int endofhead = ByteUtils.byteIndexOf(bytes, new byte[]{'\r','\n','\r','\n'}, 0);
		HashMap<String,String> headers = new HashMap<String, String>();
		for(int i=endofreqline+2;i<endofhead;){
			int next_rn = ByteUtils.byteIndexOf(bytes, new byte[]{'\r','\n'}, i);
			if(next_rn == -1)break;
			if(next_rn-i == 0){
				i = next_rn+2;
				continue;
			}
			String headerStr = new String(bytes,i,next_rn-i).trim();
			if(headerStr.indexOf(":")>0){
				headers.put(headerStr.substring(0,headerStr.indexOf(":")), headerStr.substring(headerStr.indexOf(":")+1).trim());
			}
			i = next_rn+2;
		}
		bsrHttpServletRequest.setHeaders(headers);
		
		//content_Type解析
		String content_Type = bsrHttpServletRequest.getHeader("Content-Type");
		if(content_Type !=null){
			bsrHttpServletRequest.setContentType(content_Type);
			int charset_start = content_Type.indexOf("charset=");
			int charset_end = content_Type.indexOf(";",charset_start);
			if(charset_start!=-1){
				if(charset_end !=-1){
					String charset = content_Type.substring(charset_start+8,charset_end);
					bsrHttpServletRequest.setCharacterEncoding(charset);
				}else{
					String charset = content_Type.substring(charset_start+8);
					bsrHttpServletRequest.setCharacterEncoding(charset);
				}
			}else{
				bsrHttpServletRequest.setCharacterEncoding(default_characterencoding);
			}
		}
		//COOKIE解析
		String cookies_str = bsrHttpServletRequest.getHeader("Cookie");
		if(cookies_str!=null){
			String[] cookie_one_array = cookies_str.split(";");
			List<Cookie> cookie_list = new ArrayList<Cookie>();
			for(String cookie_one : cookie_one_array){
				if(cookie_one.indexOf("=")>0){
					String cookie_key = cookie_one.substring(0,cookie_one.indexOf("="));
					String cookie_value = cookie_one.substring(cookie_one.indexOf("=")+1);
					if(StringUtils.isNotEmpty(cookie_key)&&StringUtils.isNotEmpty(cookie_value)){
						Cookie cookie = new Cookie(cookie_key.trim(), cookie_value.trim());
						cookie_list.add(cookie);
					}
				}
			}
			Cookie[] request_cookie = new Cookie[cookie_list.size()];
			
			for(int i=0;i<request_cookie.length;i++){
				request_cookie[i]=cookie_list.get(i);
			}
			
			bsrHttpServletRequest.setCookies(request_cookie);
		}
		
		
		//根据字符集解析URL
		bsrHttpServletRequest.setRequestURI(URLDecoder.decode(requestURI,bsrHttpServletRequest.getCharacterEncoding()));
		String host = bsrHttpServletRequest.getHeader("Host");
		if(host == null){
			host = "";
		}
		bsrHttpServletRequest.setRequestURL(bsrHttpServletRequest.getScheme()+"://"+ host +bsrHttpServletRequest.getRequestURI());
		//解析queryString
		int last_ = bsrHttpServletRequest.getRequestURI().lastIndexOf("/");
		if(last_>=0){
			int first_wen = bsrHttpServletRequest.getRequestURI().indexOf("?",last_);
			if(first_wen>=0){
				bsrHttpServletRequest.setQueryString(bsrHttpServletRequest.getRequestURI().substring(first_wen+1));
			}else{
				bsrHttpServletRequest.setQueryString("");
			}
		}
		
		//系统信息
		bsrHttpServletRequest.setServerName(host.split(":")[0]);
		bsrHttpServletRequest.setServerPort(Integer.parseInt(host.split(":")[1]));
		
		//解析报文体内容
		String context_length = headers.get("Content-Length");
		String transfer_encoding = headers.get("Transfer-Encoding");
		String content = null;
		if(context_length!=null){
			//如果类型是定长报文，则读取该长度的报文,并转化特殊字符
			ByteArrayOutputStream request_bao = new ByteArrayOutputStream();
			request_bao.write(bytes, endofhead+4, Integer.parseInt(context_length));
			content = new String(request_bao.toByteArray(),bsrHttpServletRequest.getCharacterEncoding());
			bsrHttpServletRequest.setContentLength(Integer.parseInt(context_length));
		}else if(transfer_encoding!=null && transfer_encoding.equals("chunked")){
			ByteArrayOutputStream request_bao = new ByteArrayOutputStream();
			int start = endofhead+4;
			while(true){
				int end = ByteUtils.byteIndexOf(bytes, new byte[]{'\r','\n'}, start);
				if(end <=start){
					//如果已经到达末尾，则退出
					break;
				}
				// 3 4 5 6
				// 1 0 0 \r \n
				String chunk_length = new String (bytes,start,end - start);
				if(Integer.parseInt(chunk_length)==0){
					//如果已经到了最后一个chunk
					break;
				}
				request_bao.write(bytes,end+2,Integer.parseInt(chunk_length));
				start +=2;
			}
			content = new String(request_bao.toByteArray(),bsrHttpServletRequest.getCharacterEncoding());
		}else {
			//此分支不可能出现
		}
		content = content.replaceAll("\\+", " ");//特殊字符处理
		content = URLDecoder.decode(content,bsrHttpServletRequest.getCharacterEncoding());
		bsrHttpServletRequest.setInputStream(new BSRServletInputStream(content));
		//解析请求url中的param
		String queryStr = bsrHttpServletRequest.getQueryString();
		if(StringUtils.isNotEmpty(queryStr)){
			String[] query_name_value_array = queryStr.split("&");
			for(String query_name_value : query_name_value_array){
				if(query_name_value.indexOf("=")>0){
					String name = query_name_value.split("=")[0];
					String value = query_name_value.split("=")[0];
					bsrHttpServletRequest.putParameter(name, value);
				}
				
			}
		}
		//解析报文内容中的param
		String[] param_key_value_s = content.split("&");
		for(String param_key_value:param_key_value_s){
			if(param_key_value.indexOf("=")>0){
				String name = param_key_value.split("=")[0];
				String value = param_key_value.split("=")[1];
				bsrHttpServletRequest.putParameter(name, value);
			}
		}
		
		
		
		return bsrHttpServletRequest;
	}
	
}
