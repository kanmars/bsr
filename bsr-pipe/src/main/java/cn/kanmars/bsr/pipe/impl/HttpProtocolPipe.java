package cn.kanmars.bsr.pipe.impl;

import java.net.InetSocketAddress;

import cn.kanmars.bsr.http.request.BSRHttpServletRequest;
import cn.kanmars.bsr.http.request.BSRHttpServletRequestParser;
import cn.kanmars.bsr.http.response.BSRHttpServletResponse;
import cn.kanmars.bsr.http.response.BSRHttpServletResponseParser;
import cn.kanmars.bsr.http.util.MIMEUtils;
import cn.kanmars.bsr.http.util.StringUtils;
import cn.kanmars.bsr.server.context.BSRContext;
import cn.kanmars.bsr.server.event.BSREvents;
import cn.kanmars.bsr.server.log.Logger;
import cn.kanmars.bsr.server.pipe.BSRPipe;

/**
 * Http协议解析管道,
 * @author baolong
 *
 */
public class HttpProtocolPipe extends BSRPipe{
	public void execute(String bsrEvents,Object ... objs) throws Exception {
		try {
			if(bsrEvents.equals(BSREvents.OP_READ)){
				BSRContext bsrContext = (BSRContext)objs[0];
				String req = new String(bsrContext.getReadContent());
				Logger.debug("客户端["+bsrContext.getUuid()+"]请求为["+req+"]");
				if(BSRHttpServletRequestParser.isReceivedAll(bsrContext.getReadBao())){
					//开始操作数据
					BSRHttpServletRequest bsrHttpServletRequest = (BSRHttpServletRequest) BSRHttpServletRequestParser.parse(bsrContext.getReadBao());
					bsrContext.getReadBao().reset();
					bsrHttpServletRequest.setRemoteAddr(bsrContext.getSocketChannel().getRemoteAddress().toString());
					bsrHttpServletRequest.setRemoteHost(((InetSocketAddress)bsrContext.getSocketChannel().getRemoteAddress()).getHostString());
					/**常用的报文*/
					BSRHttpServletResponse bsrHttpServletResponse = BSRHttpServletResponseParser.createResponse(200,"OK", null);
//					/**重定向报文*/
//					BSRHttpServletResponse response2 = BSRHttpServletResponseParser.createSendRedirectResponse(301, "Moved Permanently", null, "http://www.baidu.com");

					/**根据request中的末尾，设置contentType*/			
					String content_Type = getContentTypeByFileSuffix(bsrHttpServletRequest,bsrHttpServletResponse);
					bsrHttpServletResponse.setContentType(content_Type);
					/**
					 * 在诸多请求都准备好之后，执行下一步管道
					 */
					doNext(bsrEvents,bsrHttpServletRequest,bsrHttpServletResponse);
					
					/**设置响应报文*/
					if(bsrHttpServletRequest.getHeader("Accept-Encoding").indexOf("gzip")>=0){
						//如果需要gzip压缩，则转换为gzip报文
						bsrContext.getWriteBao().write(BSRHttpServletResponseParser.transResponseToGzipBytes(bsrHttpServletResponse));
					}else{
						//如果不需要，则返回正常报文
						bsrContext.getWriteBao().write(BSRHttpServletResponseParser.transResponseToBytes(bsrHttpServletResponse));
					}
					
					//在报文操作成功后，转变bsrContext到写事件监听
					bsrContext.changeSelectorRegister2Write();
				
				}
			}else if(bsrEvents.equals(BSREvents.OP_WRITE)){
				
				BSRContext bsrContext = (BSRContext)objs[0];
				if(bsrContext.isLive()){
					//写出数据
					bsrContext.write(bsrContext.getWriteBao().toByteArray());
					//输出后，清空内容
					bsrContext.cleanContent();
					//在报文写出成功后，转变bsrContext到读事件监听
					bsrContext.changeSelectorRegister2Read();
				}
			}else if(bsrEvents.equals(BSREvents.OP_CLOSE)){
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 获取请求文件的mime
	 * @param bsrHttpServletRequest
	 * @param bsrHttpServletResponse
	 * @return
	 */
	public String getContentTypeByFileSuffix(BSRHttpServletRequest bsrHttpServletRequest, BSRHttpServletResponse bsrHttpServletResponse){
		String reqURI = bsrHttpServletRequest.getRequestURI();
		String content_Type = bsrHttpServletResponse.getContentType();
		if(StringUtils.isEmpty(content_Type)){
			content_Type = "text/html";
		}
		int wen = reqURI.lastIndexOf("?");//最后一个问号?的位置
		int dot = -1 ;
		if(wen <0){
			dot = reqURI.lastIndexOf(".");//如果没有问号，从最后开始索引
		}else{
			dot = reqURI.lastIndexOf(".",wen);//如果有问号，则查找问号后的最后一个.
		}
		//在普通的情况下
		if(dot > 0){
			if(wen > 0){
				//有问号
				String fileSuffix = reqURI.substring(dot,wen);
				//获取后缀名
				content_Type = MIMEUtils.getMIME(fileSuffix);
			}else{
				//无问号
				String fileSuffix = reqURI.substring(dot);
				//获取后缀名
				content_Type = MIMEUtils.getMIME(fileSuffix);
			}
		}else{
			//如果没有.，则返回"text/html"
			content_Type = "text/html";
		}
		
		return content_Type;
	}
}
