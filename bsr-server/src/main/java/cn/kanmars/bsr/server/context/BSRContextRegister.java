package cn.kanmars.bsr.server.context;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;
import cn.kanmars.bsr.server.log.Logger;

public class BSRContextRegister {
	/**
	 * 全局BSRContext注册表
	 */
	private static ConcurrentHashMap<SocketChannel,BSRContext> BSRContextTree = new ConcurrentHashMap<SocketChannel,BSRContext>();
	/**
	 * 可操作的BSRContext队列
	 */
	private static LinkedBlockingQueue<BSRContext> readableBSRContext = new LinkedBlockingQueue<BSRContext>();
	
	
	/**
	 * 获取全局注册表
	 * @return
	 */
	public static ConcurrentHashMap<SocketChannel, BSRContext> getBSRContextTree() {
		return BSRContextTree;
	}
	
	/**
	 *从全局注册表中注册一个BSRContext
	 * @param bsrContext
	 * @return
	 */
	public static boolean registerBSRContext(SocketChannel socketChannel,BSRContext bsrContext){
		//如果链接数量超过了最大链接数量
		if(BSRContextTree.size()>Long.parseLong(BSRConfiger.getConfig(BSRConstants.MAXCONNECTIONS))){
			return false;
		}
		BSRContextTree.put(socketChannel,bsrContext);
		return true;
	}
	/**
	 * 从全局注册表中删除一个BSRContext
	 * @param bsrContext
	 * @return
	 */
	public static boolean removeBSRContext(SocketChannel socketChannel){
		if(socketChannel == null){
			return false;
		}
		try{
			BSRContext bsrContext_ = BSRContextTree.remove(socketChannel);
			if(bsrContext_!=null)bsrContext_.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 从全局注册表中获取一个BSRContext
	 * @param bsrContext
	 * @return
	 */
	public static BSRContext getBSRContext(SocketChannel socketChannel){
		return BSRContextTree.get(socketChannel);
	}
	
	/**
	 * 从可操作的BSRContext队列中获取一个可操作上下文
	 * @return
	 */
	public static BSRContext getReadableBSRContext(){
		try {
			return readableBSRContext.poll(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Logger.debug("发生异常");
			return null;
		}
	}
	
	/**
	 * 从可操作的BSRContext队列中添加一个可操作上下文
	 * @return
	 */
	public static boolean addReadableBSRContext(BSRContext bsrContext){
		if(!readableBSRContext.contains(bsrContext)){
			//如果不包含，则增加进去
			if(bsrContext != null){
				return readableBSRContext.add(bsrContext);
			}else{
				return false;
			}
		}else{
			//如果包含，则直接返回true
			return true;
		}
	}
	
}
