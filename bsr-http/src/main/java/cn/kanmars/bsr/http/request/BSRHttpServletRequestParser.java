package cn.kanmars.bsr.http.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import cn.kanmars.bsr.http.stream.BSRServletInputStream;
import cn.kanmars.bsr.http.util.ByteUtils;

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
				String[] header_arrays = headerStr.split(":");
				headers.put(header_arrays[0].trim(), header_arrays[1].trim());
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
		//根据字符集解析URL
		bsrHttpServletRequest.setRequestURI(URLDecoder.decode(requestURI,bsrHttpServletRequest.getCharacterEncoding()));
		String host = bsrHttpServletRequest.getHeader("Host");
		if(host == null){
			host = "localhost:1234";
		}
		bsrHttpServletRequest.setRequestURL(bsrHttpServletRequest.getScheme()+"://"+ host +bsrHttpServletRequest.getRequestURI());
		//解析queryString
		bsrHttpServletRequest.setQueryString(bsrHttpServletRequest.getRequestURI().substring(bsrHttpServletRequest.getRequestURI().lastIndexOf("/")));
		
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
		content.replaceAll("+", " ");//特殊字符处理
		content = URLDecoder.decode(content,bsrHttpServletRequest.getCharacterEncoding());
		bsrHttpServletRequest.setInputStream(new BSRServletInputStream(content));
		//解析param
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
