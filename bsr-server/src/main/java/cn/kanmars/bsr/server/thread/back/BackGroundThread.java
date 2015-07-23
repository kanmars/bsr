package cn.kanmars.bsr.server.thread.back;

import java.nio.channels.SocketChannel;
import java.util.Map.Entry;

import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;
import cn.kanmars.bsr.server.context.BSRContext;
import cn.kanmars.bsr.server.context.BSRContextRegister;
import cn.kanmars.bsr.server.log.Logger;
import cn.kanmars.bsr.server.thread.ThreadRegister;

public class BackGroundThread extends Thread {

	@Override
	public void run() {
		Logger.info("BackGroundThread开始运行");
		while(true){
			int count = 0;
			for(Entry<SocketChannel, BSRContext> entry:BSRContextRegister.getBSRContextTree().entrySet()){
				SocketChannel socketChannel = entry.getKey();
				BSRContext bsrContext = entry.getValue();
				
				//如果已经标志为死亡
				if(bsrContext.isLive() == false){
					BSRContextRegister.removeBSRContext(socketChannel);
					count++;
					continue;
				}
				
				//如果创建时间+最大超时时间小于当前时间，则removeBSRContext
				if(bsrContext.getCreateTime()+Long.parseLong(BSRConfiger.getConfig(BSRConstants.MAXCONNTIME)) < System.currentTimeMillis()){
					BSRContextRegister.removeBSRContext(socketChannel);
					count++;
					continue;
				}
				
				//如操作时间+超时时间小于当前时间，则removeBSRContext
				if(bsrContext.getOperationTime()+Long.parseLong(BSRConfiger.getConfig(BSRConstants.TIMEOUT)) < System.currentTimeMillis()){
					BSRContextRegister.removeBSRContext(socketChannel);
					count++;
					continue;
				}
			}
			Logger.info("本次清理，清理线程["+count+"]个");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void bootstrap(){
		for(int i=0;i<Integer.parseInt(BSRConfiger.getConfig(BSRConstants.BACK_THREAD_NUMBER));i++){
			BackGroundThread bgt = new BackGroundThread();
			ThreadRegister.backgroundExecutorService.execute(bgt);
		}
	}
	
}
