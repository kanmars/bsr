package cn.kanmars.bsr.server.pipe.impl;

import java.net.InetSocketAddress;

import cn.kanmars.bsr.http.request.BSRHttpServletRequest;
import cn.kanmars.bsr.http.request.BSRHttpServletRequestParser;
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
			//String response = "<html><form action=\"http://localhost:1234/kkk\"><input name='a' value='aa'/><input name='b' value='bb'/><input type='commit' value='cll'></form></html>";
			bsrContext.write(req.getBytes());	
		}else if(bsrEvents.equals(BSREvents.OP_CLOSE)){
			System.out.println("发生远程客户端关闭事件");
		}
		
		
		doNext(bsrContext_,bsrEvents);
	}
}
