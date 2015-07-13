package cn.kanmars.bsr.server.pipe.impl;

import cn.kanmars.bsr.server.context.BSRContext;
import cn.kanmars.bsr.server.event.BSREvents;
import cn.kanmars.bsr.server.pipe.BSRPipe;

public class EchoPipeLine extends BSRPipe {

	public void execute(Object bsrContext_, String bsrEvents) {
		
		if(bsrEvents.equals(BSREvents.OP_READ)){
			BSRContext bsrContext = (BSRContext)bsrContext_;
			String req = new String(bsrContext.getContent());
			System.out.println("客户端请求为["+req+"]");
			bsrContext.write(req.getBytes());	
		}else if(bsrEvents.equals(BSREvents.OP_CLOSE)){
			System.out.println("发生远程客户端关闭事件");
		}
		
		
		doNext(bsrContext_,bsrEvents);
	}
}
