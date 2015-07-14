package cn.kanmars.bsr.http.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import cn.kanmars.bsr.http.stream.BSRServletOutputStream;
import cn.kanmars.bsr.http.util.DateUtils;
import cn.kanmars.bsr.http.util.StringUtils;

public class BSRHttpServletResponseParser {
	/**
	 * 创建一个指定内容的响应
	 * @param status	状态
	 * @param sm		状态描述
	 * @param header	报文header域
	 * @param content	响应报文
	 * @return
	 */
	public static BSRHttpServletResponse createResponse(int status,String sm,Map<String,String> headers,String content){
		
		BSRHttpServletResponse response = new BSRHttpServletResponse();
		response.setStatus(status);
		response.setStatus_message(sm);
		response.setHeaders(headers);
		try {
			response.getOutputStream().write(content.getBytes("ISO8859-1"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	/**
	 * 创建一个无内容的响应对象
	 * @param status		状态
	 * @param sm		状态描述
	 * @param header	报文header域
	 * @return
	 */
	public static BSRHttpServletResponse createResponse(int status,String sm,Map<String,String> headers){
		BSRHttpServletResponse response = new BSRHttpServletResponse();
		response.setStatus(status);
		response.setStatus_message(sm);
		response.setHeaders(headers);
		return response;
	}
	
	/**
	 * 创建一个重定向的响应对象
	 * @param status		状态	301	永久重定向	302	临时重定向
	 * @param sm		状态描述	Moved Permanently永久重定向   Moved Temporarily临时重定向
	 * @param header	报文header域
	 * @param outputStream	输出流
	 * @return
	 */
	public static BSRHttpServletResponse createSendRedirectResponse(int status,String sm,Map<String,String> headers,String location){
		BSRHttpServletResponse response = new BSRHttpServletResponse();
		response.setStatus(status);//302
		response.setStatus_message(sm);
		response.setHeaders(headers);
		response.setHeader("Location", location);
		return response;
	}
	
	/**
	 * 将response转化为byte数组，在转化时，修改header域，生成Content-Length等信息
	 * @param response
	 * @throws Exception 
	 */
	public static byte[] transResponseToBytes(BSRHttpServletResponse response) throws Exception{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bao.write((response.getProtocol()+" "+response.getStatus()+" "+response.getStatus_message()+"\r\n").getBytes());
		if(StringUtils.isNotEmpty(response.getContentType())){
			response.setHeader("Content-Type", response.getContentType());
		}
		
		byte[] contentBytes = ((BSRServletOutputStream)response.getOutputStream()).getContentBytes();
		//设置报文长度
		response.setContentLength(contentBytes.length);
		
		if(response.getContentLength() >=0){
			response.setHeader("Content-Length", ""+response.getContentLength());
		}
		response.setHeader("Date", DateUtils.getGMTStr());
		
		Map<String, String> headers = response.getHeaders();
		
		for(Entry<String, String> e : headers.entrySet()){
			bao.write((e.getKey()+": "+e.getValue()+"\r\n").getBytes());
		}
		//头部结束
		bao.write("\r\n".getBytes());
		
		bao.write(contentBytes);
		
		return bao.toByteArray();
	}
	
	
}
