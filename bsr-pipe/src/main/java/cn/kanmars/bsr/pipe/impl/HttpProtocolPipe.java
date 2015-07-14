package cn.kanmars.bsr.pipe.impl;

import java.io.IOException;
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
	public void execute(String bsrEvents,Object ... objs) {
		if(bsrEvents.equals(BSREvents.OP_READ)){
			BSRContext bsrContext = (BSRContext)objs[0];
			String req = new String(bsrContext.getContent());
			Logger.debug("客户端请求为["+req+"]");
			
			try {
				if(BSRHttpServletRequestParser.isReceivedAll(bsrContext.getBao())){
					
					BSRHttpServletRequest bsrHttpServletRequest = (BSRHttpServletRequest) BSRHttpServletRequestParser.parse(bsrContext.getBao());
					bsrContext.getBao().reset();
					bsrHttpServletRequest.setRemoteAddr(bsrContext.getSocketChannel().getRemoteAddress().toString());
					bsrHttpServletRequest.setRemoteHost(((InetSocketAddress)bsrContext.getSocketChannel().getRemoteAddress()).getHostString());
					/**常用的报文*/
					BSRHttpServletResponse bsrHttpServletResponse = BSRHttpServletResponseParser.createResponse(200,"OK", null);
					try {
						bsrHttpServletResponse.getOutputStream().write("这是一篇非常长非常长的文章".getBytes("GBK"));
						bsrHttpServletResponse.setContentType("text/html; charset=GBK");
					} catch (IOException e) {
						e.printStackTrace();
					}
//					/**重定向报文*/
//					BSRHttpServletResponse response2 = BSRHttpServletResponseParser.createSendRedirectResponse(301, "Moved Permanently", null, "http://www.baidu.com");

					/**根据request中的末尾，设置contentType*/			
					String reqURI = bsrHttpServletRequest.getRequestURI();//带参数的
					String content_Type = bsrHttpServletResponse.getContentType();
					if(StringUtils.isEmpty(content_Type)){
						content_Type = "text/html";
					}
					int wen = reqURI.lastIndexOf("?");//最后一个问号?的位置
					int xiegang = reqURI.lastIndexOf("/");//最后一个斜杠/的位置
					int dot = reqURI.indexOf(".");//从斜杠开始的第一个.的位置
					if((xiegang<wen && dot < wen && dot > 0 && xiegang >= 0 ) || (wen < 0 && xiegang>=0 && dot >= 0) ){
						//在普通的情况下
						if(wen > 0){
							String fileSuffix = reqURI.substring(dot,wen);
							//获取后缀名
							content_Type = MIMEUtils.getMIME(fileSuffix);
						}else{
							String fileSuffix = reqURI.substring(dot);
							//获取后缀名
							content_Type = MIMEUtils.getMIME(fileSuffix);
						}
					}
					bsrHttpServletResponse.setContentType(content_Type);
					/**
					 * 在诸多请求都准备好之后，执行下一步管道
					 */
					doNext(bsrEvents,bsrHttpServletRequest,bsrHttpServletResponse);
					/**报文发送*/
					try {
						bsrContext.write(BSRHttpServletResponseParser.transResponseToBytes(bsrHttpServletResponse));
					} catch (Exception e) {
						e.printStackTrace();
					}	
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else if(bsrEvents.equals(BSREvents.OP_CLOSE)){
			System.out.println("发生远程客户端关闭事件");
		}
		
		
	}
}
