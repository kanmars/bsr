package cn.kanmars.bsr.server.thread.worker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import cn.kanmars.bsr.server.cache.BSRPoolsHolder;
import cn.kanmars.bsr.server.cache.bytebufferpool.BSRByteBufferPool;
import cn.kanmars.bsr.server.context.BSRContext;
import cn.kanmars.bsr.server.context.BSRContextRegister;
import cn.kanmars.bsr.server.event.BSREvents;
import cn.kanmars.bsr.server.log.Logger;
import cn.kanmars.bsr.server.pipe.BSRPipeProcessor;

/**
 * 工人线程
 * @author baolong
 *
 */
public class WorkerThread extends Thread {

	private BSRPipeProcessor bsrPipeProcessor;
	
	public WorkerThread() {
		super();
	}

	public WorkerThread(BSRPipeProcessor bsrPipeProcessor) {
		super();
		this.bsrPipeProcessor = bsrPipeProcessor;
	}
	
	public BSRPipeProcessor getBsrPipeProcessor() {
		return bsrPipeProcessor;
	}

	public void setBsrPipeProcessor(BSRPipeProcessor bsrPipeProcessor) {
		this.bsrPipeProcessor = bsrPipeProcessor;
	}

	public void run() {
		Logger.info("WorkerThread开始运行");
		while(true){
			try {
				BSRContext bsrContext = BSRContextRegister.getReadableBSRContext();
				if(bsrContext == null){
					continue;
				}
				//Logger.debug("获取到读取事件，开始处理");
				SocketChannel socketChannel = bsrContext.getSocketChannel();
				
				
				if(!socketChannel.isConnected()){
					BSRContextRegister.removeBSRContext(socketChannel);
					continue;
				}
				try{
					//从缓冲池中获取ByteBuffer
					ByteBuffer byteBuffer = BSRPoolsHolder.getBSRByteBufferPool().requireT();
					//读取数据
					while(true){
						int length =socketChannel.read(byteBuffer);
						if(length >0){
							//将读取到的byte放入到BSRContext
							if(byteBuffer.hasArray()){
								bsrContext.getBao().write(byteBuffer.array(),0,length);
							}else{
								byte[] tmp = new byte[byteBuffer.position()];
								byteBuffer.get(tmp);
								bsrContext.getBao().write(tmp,0,tmp.length);
							}
							//本次读取完毕，开始管道线处理
							bsrPipeProcessor.execute(bsrContext, BSREvents.OP_READ);
						}else if(length ==0){
							//如果本次事件触发，内层循环中长度为0，则忽略本次事件
							break;
						}else if(length == -1){
							//如果读取到-1则为已关闭状态
							Logger.debug("socket关闭");
							bsrPipeProcessor.execute(bsrContext, BSREvents.OP_CLOSE);
							BSRContextRegister.removeBSRContext(socketChannel);
						}else{
							Logger.debug("通讯异常");
						}
					}
					//将ByteBuffer释放到缓存中
					BSRPoolsHolder.getBSRByteBufferPool().releaseT(byteBuffer);
					
				}catch(ClosedChannelException e){
					Logger.debug("socket关闭");
					bsrPipeProcessor.execute(bsrContext, BSREvents.OP_CLOSE);
					BSRContextRegister.removeBSRContext(socketChannel);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
