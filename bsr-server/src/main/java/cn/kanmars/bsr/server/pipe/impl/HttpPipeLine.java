package cn.kanmars.bsr.server.pipe.impl;

import java.io.IOException;
import java.net.InetSocketAddress;

import cn.kanmars.bsr.http.request.BSRHttpServletRequest;
import cn.kanmars.bsr.http.request.BSRHttpServletRequestParser;
import cn.kanmars.bsr.http.response.BSRHttpServletResponse;
import cn.kanmars.bsr.http.response.BSRHttpServletResponseParser;
import cn.kanmars.bsr.server.context.BSRContext;
import cn.kanmars.bsr.server.event.BSREvents;
import cn.kanmars.bsr.server.pipe.BSRPipe;

public class HttpPipeLine extends BSRPipe{
public void execute(Object bsrContext_, String bsrEvents) {
		
		if(bsrEvents.equals(BSREvents.OP_READ)){
			BSRContext bsrContext = (BSRContext)bsrContext_;
			String req = new String(bsrContext.getContent());
			System.out.println("客户端请求为["+req+"]");
			
			try {
				if(BSRHttpServletRequestParser.isReceivedAll(bsrContext.getBao())){
					BSRHttpServletRequest bsrHttpServletRequest = (BSRHttpServletRequest) BSRHttpServletRequestParser.parse(bsrContext.getBao());
					bsrHttpServletRequest.setRemoteAddr(bsrContext.getSocketChannel().getRemoteAddress().toString());
					bsrHttpServletRequest.setRemoteHost(((InetSocketAddress)bsrContext.getSocketChannel().getRemoteAddress()).getHostString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			/**常用的报文*/
			BSRHttpServletResponse response = BSRHttpServletResponseParser.createResponse(200,"OK", null);
			try {
				response.getOutputStream().write("这是一篇非常长非常长的文章".getBytes("GBK"));
				response.setContentType("text/html; charset=GBK");
			} catch (IOException e) {
				e.printStackTrace();
			}
			/**重定向报文*/
			BSRHttpServletResponse response2 = BSRHttpServletResponseParser.createSendRedirectResponse(301, "Moved Permanently", null, "http://www.baidu.com");

			/**报文发送*/
			try {
				bsrContext.write(BSRHttpServletResponseParser.transResponseToBytes(response2));
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}else if(bsrEvents.equals(BSREvents.OP_CLOSE)){
			System.out.println("发生远程客户端关闭事件");
		}
		
		doNext(bsrContext_,bsrEvents);
	}
}
