package cn.kanmars.bsr.server.thread.worker;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import cn.kanmars.bsr.server.cache.BSRPoolsHolder;
import cn.kanmars.bsr.server.context.BSRContext;
import cn.kanmars.bsr.server.context.BSRContextRegister;
import cn.kanmars.bsr.server.event.BSREvents;
import cn.kanmars.bsr.server.log.Logger;
import cn.kanmars.bsr.server.pipe.AbstracePipelineProcessor;
import cn.kanmars.bsr.server.pipe.SimpleBSRPipeProcessor;
import cn.kanmars.bsr.server.thread.ThreadRegister;

/**
 * 工人线程
 * @author baolong
 *
 */
public class WorkerThread extends Thread {

	private AbstracePipelineProcessor bsrPipeProcessor;
	
	public WorkerThread() {
		super();
	}
	

	public WorkerThread(AbstracePipelineProcessor bsrPipeProcessor) {
		super();
		this.bsrPipeProcessor = bsrPipeProcessor;
	}

	public WorkerThread(SimpleBSRPipeProcessor bsrPipeProcessor) {
		super();
		this.bsrPipeProcessor = bsrPipeProcessor;
	}
	
	public AbstracePipelineProcessor getBsrPipeProcessor() {
		return bsrPipeProcessor;
	}

	public void setBsrPipeProcessor(AbstracePipelineProcessor bsrPipeProcessor) {
		this.bsrPipeProcessor = bsrPipeProcessor;
	}

	public void run() {
		Logger.info("WorkerThread开始运行");
		while(true){
			try {
				BSRContext bsrContext = BSRContextRegister.getOperableBSRContext();
				if(bsrContext == null){
					continue;
				}
				//尝试将bsrContext的拥有者修改为当前线程
				if(!bsrContext.changeOwner(this)){
					continue;
				}
				if(bsrContext.isRead()){
					Logger.debug("获取到UUID["+bsrContext.getUuid()+"]可读事件，开始处理");
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
									bsrContext.getReadBao().write(byteBuffer.array(),0,length);
								}else{
									byte[] tmp = new byte[byteBuffer.position()];
									byteBuffer.get(tmp);
									bsrContext.getReadBao().write(tmp,0,tmp.length);
								}
								//本次读取完毕，开始管道线处理
								bsrPipeProcessor.execute(BSREvents.OP_READ,bsrContext);
							}else if(length ==0){
								//如果本次事件触发，内层循环中长度为0，则忽略本次事件
								break;
							}else if(length == -1){
								//如果读取到-1则为已关闭状态
								Logger.debug("read-1.socket关闭");
								bsrPipeProcessor.execute(BSREvents.OP_CLOSE,bsrContext);
								BSRContextRegister.removeBSRContext(socketChannel);
							}else{
								Logger.debug("通讯异常");
							}
							
						}
						//将ByteBuffer释放到缓存中
						BSRPoolsHolder.getBSRByteBufferPool().releaseT(byteBuffer);
						
					}catch(ClosedChannelException e){
						Logger.debug("ClosedChannelException.socket关闭");
						bsrPipeProcessor.execute(BSREvents.OP_CLOSE,bsrContext);
						BSRContextRegister.removeBSRContext(socketChannel);
					}catch(Exception e){
						Logger.debug("发生其他异常.socket关闭");
						bsrPipeProcessor.execute(BSREvents.OP_CLOSE,bsrContext);
						BSRContextRegister.removeBSRContext(socketChannel);
					}
				}else if(bsrContext.isWrite()){
					Logger.debug("获取到UUID["+bsrContext.getUuid()+"]可写事件，开始处理");
					//Logger.debug("获取到写事件，开始处理");
					SocketChannel socketChannel = bsrContext.getSocketChannel();
					if(!socketChannel.isConnected()){
						BSRContextRegister.removeBSRContext(socketChannel);
						continue;
					}
					//管道线处理写事件
					try{
						bsrPipeProcessor.execute(BSREvents.OP_WRITE,bsrContext);
					}catch(Exception e){
						Logger.debug("发生其他异常.socket关闭");
						bsrPipeProcessor.execute(BSREvents.OP_CLOSE,bsrContext);
						BSRContextRegister.removeBSRContext(socketChannel);
					}
				}
				bsrContext.cleanOwner(this);
				
			} catch (Exception e) {
				//总体异常
				e.printStackTrace();
			}
		}
	}
	
	public void startup(){
		ThreadRegister.workerExecutorService.execute(this);
	}

}
