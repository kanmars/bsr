package cn.kanmars.bsr.http.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.Cookie;

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
	 * 将response转化为byte数组，在转化时，修改header域，生成Content-Length等信息，正常报文无压缩
	 * @param response
	 * @throws Exception 
	 */
	public static byte[] transResponseToBytes(BSRHttpServletResponse response) throws Exception{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bao.write((response.getProtocol()+" "+response.getStatus()+" "+response.getStatus_message()+"\r\n").getBytes());
		if(StringUtils.isNotEmpty(response.getContentType())){
			response.setHeader("Content-Type", response.getContentType());
		}
		response.setHeader("Date", DateUtils.getGMTStr());
		
		byte[] contentBytes = ((BSRServletOutputStream)response.getOutputStream()).getContentBytes();
		//设置报文长度
		int chunkLength = 5120;
		if(contentBytes.length<=chunkLength){
			//报文长度小于某个长度，则直接设置长度
			response.setContentLength(contentBytes.length);
			if(response.getContentLength() >=0){
				response.setHeader("Content-Length", ""+response.getContentLength());
			}
			
			Map<String, String> headers = response.getHeaders();
			
			for(Entry<String, String> e : headers.entrySet()){
				bao.write((e.getKey()+": "+e.getValue()+"\r\n").getBytes());
			}
			//增加cookie信息
			addCookiesInfo(response,bao);
			//头部结束
			bao.write("\r\n".getBytes());
			
			bao.write(contentBytes);
			bao.write(("\r\n").getBytes());
		}else{
			//chunked报文组装
			Map<String, String> headers = response.getHeaders();
			headers.put("Transfer-Encoding", "chunked");
			
			
			for(Entry<String, String> e : headers.entrySet()){
				bao.write((e.getKey()+": "+e.getValue()+"\r\n").getBytes());
			}
			//增加cookie信息
			addCookiesInfo(response,bao);
			//头部结束
			bao.write("\r\n".getBytes());
			for(int i=0;i<contentBytes.length;i+=chunkLength){
				if(i!=0){
					bao.write(("\r\n").getBytes());
				}
				if(i+chunkLength<contentBytes.length){
					//如果长度够一个chunk
					bao.write((""+transD216X(chunkLength)+"\r\n").getBytes());
					bao.write(contentBytes,i,chunkLength);
					bao.write(("\r\n").getBytes());
				}else{
					//如果长度不够一个chunk
					int length = contentBytes.length - i;
					bao.write((""+transD216X(length)+"\r\n").getBytes());
					bao.write(contentBytes,i,length);
					bao.write(("\r\n").getBytes());
				}
			}
			bao.write(("0\r\n\r\n").getBytes());
		}
		
		return bao.toByteArray();
	}
	
	/**
	 * 将response转化为byte数组，在转化时，修改header域，生成Content-Length等信息，采用gzip压缩
	 * @param response
	 * @throws Exception 
	 */
	public static byte[] transResponseToGzipBytes(BSRHttpServletResponse response) throws Exception{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bao.write((response.getProtocol()+" "+response.getStatus()+" "+response.getStatus_message()+"\r\n").getBytes());
		if(StringUtils.isNotEmpty(response.getContentType())){
			response.setHeader("Content-Type", response.getContentType());
		}
		response.setHeader("Content-Encoding", "gzip");
		response.setHeader("Date", DateUtils.getGMTStr());
		
		byte[] contentBytes = ((BSRServletOutputStream)response.getOutputStream()).getContentBytes();
		
		//进行压缩
		contentBytes = transBytes2GzipBytes(contentBytes,0,contentBytes.length);
		
		//设置报文长度
		int chunkLength = 5120;
		if(contentBytes.length<=chunkLength){
			//报文长度小于某个长度，则直接设置长度
			response.setContentLength(contentBytes.length);
			if(response.getContentLength() >=0){
				response.setHeader("Content-Length", ""+response.getContentLength());
			}
			
			Map<String, String> headers = response.getHeaders();
			
			for(Entry<String, String> e : headers.entrySet()){
				bao.write((e.getKey()+": "+e.getValue()+"\r\n").getBytes());
			}
			//增加cookie信息
			addCookiesInfo(response,bao);
			//头部结束
			bao.write("\r\n".getBytes());
			
			bao.write(contentBytes);
			bao.write(("\r\n").getBytes());
		}else{
			//chunked报文组装
			Map<String, String> headers = response.getHeaders();
			headers.put("Transfer-Encoding", "chunked");
			
			
			for(Entry<String, String> e : headers.entrySet()){
				bao.write((e.getKey()+": "+e.getValue()+"\r\n").getBytes());
			}
			//增加cookie信息
			addCookiesInfo(response,bao);
			//头部结束
			bao.write("\r\n".getBytes());
			for(int i=0;i<contentBytes.length;i+=chunkLength){
				if(i!=0){
					bao.write(("\r\n").getBytes());
				}
				if(i+chunkLength<contentBytes.length){
					//如果长度够一个chunk
					bao.write((""+transD216X(chunkLength)+"\r\n").getBytes());
					bao.write(contentBytes,i,chunkLength);
					bao.write(("\r\n").getBytes());
				}else{
					//如果长度不够一个chunk
					int length = contentBytes.length - i;
					bao.write((""+transD216X(length)+"\r\n").getBytes());
					bao.write(contentBytes,i,length);
					bao.write(("\r\n").getBytes());
				}
			}
			bao.write(("0\r\n\r\n").getBytes());
		}
		
		return bao.toByteArray();
	}
	
	public static String transD216X(int length){
		return Integer.toHexString(length);
	}
	
	public static byte[] transBytes2GzipBytes(byte[] simplebyte,int start,int length){
        try {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        	// 压缩  
        	GZIPOutputStream gos = new GZIPOutputStream(baos);  
			gos.write(simplebyte, start, length);
			gos.finish();  
			byte[] output = baos.toByteArray();  
			baos.flush();  
			baos.close();  
			return output;  
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return null;
	}
	
	public static byte[] transGzipBytes2Bytes(byte[] gzipbyte){
		 try {
			 	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        	ByteArrayInputStream bin = new ByteArrayInputStream(gzipbyte);
	        	// 压缩  
	        	GZIPInputStream gis = new GZIPInputStream(bin);
	        	
	        	byte[] temp = new byte[5120];
	        	int count = 0;
	        	while((count = gis.read(temp))>0){
	        		baos.write(temp,0,count);
	        	}
				gis.close();  
				byte[] output = baos.toByteArray();  
				baos.flush();  
				baos.close();  
				return output;  
			} catch (IOException e) {
				e.printStackTrace();
			}  
			return null;
	}
	
	/**
	 * 增加一个cookie信息
	 * @param response
	 * @param bao
	 */
	public static void addCookiesInfo(BSRHttpServletResponse response,ByteArrayOutputStream bao ){
		
//      name.equalsIgnoreCase("Path") ||		
//         name.equalsIgnoreCase("Domain") ||
//         name.equalsIgnoreCase("Expires") || // (old cookies)已被Max-Age取代
		
//		 name.equalsIgnoreCase("Comment") || // rfc2019
//         name.equalsIgnoreCase("Max-Age") || // rfc2019

//         name.equalsIgnoreCase("Secure") ||
//         name.equalsIgnoreCase("Version") ||
		try {
			
			List<Cookie> list = response.getCookies();
			if(list.size()>0){
				for(int i=0;i<list.size();i++){
					Cookie cookie = list.get(i);
					bao.write(("Set-Cookie: ").getBytes());
					if(StringUtils.isNotEmpty(cookie.getName())){
						bao.write((cookie.getName()+"="+cookie.getValue()+"; ").getBytes());
					}
					if(StringUtils.isNotEmpty(cookie.getPath())){
						bao.write(("Path="+cookie.getValue()+"; ").getBytes());
					}else{
						bao.write(("Path=/; ").getBytes());
					}
					if(StringUtils.isNotEmpty(cookie.getDomain())){
						bao.write(("Domain="+cookie.getValue()+"; ").getBytes());
					}
					bao.write(("Max-Age="+cookie.getMaxAge()+"; ").getBytes());
					if(cookie.getSecure()){
						bao.write(("SECURE; ").getBytes());
					}
					bao.write(("\r\n").getBytes());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
