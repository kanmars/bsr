package cn.kanmars.bsr.server.boot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.kanmars.bsr.server.cache.BSRPoolsHolder;
import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;
import cn.kanmars.bsr.server.jmx.BSRJMXAgent;
import cn.kanmars.bsr.server.pipe.BSRPipe;
import cn.kanmars.bsr.server.pipe.SimpleBSRPipeProcessor;
import cn.kanmars.bsr.server.pipe.impl.EchoPipeLine;
import cn.kanmars.bsr.server.socket.channel.ServerSocketChannelHolder;
import cn.kanmars.bsr.server.socket.selector.SelectorHolder;
import cn.kanmars.bsr.server.thread.back.BackGroundThread;
import cn.kanmars.bsr.server.thread.boss.BossThread;
import cn.kanmars.bsr.server.thread.worker.WorkerThread;

/**
 * BSR服务器的启动器，一个简单的echo 示例
 * @author baolong
 *
 */
public class BootStrap {
	public static void main(String[] args) throws Exception {
		
		/**加载配置文件*/
		initBSRConfiger(args);
		
		/**初始化操作*/
		initBSRServer();
		
		/**初始化选择器*/
		initSelector();
		
		/**初始化服务端channel*/
		initServerSocketChannel();
		
		/**启动boss线程*/
		runBossThread();
		
		/**启动worker线程*/
		runWorkerThread();
		
		/**启动background线程*/
		runBackThread();
		
		/**主线程等待*/
		while(true){
			Thread.sleep(1000000);
		}
	}
	
	/**
	 * BSRServer进行初始化配置文件
	 */
	public static void initBSRConfiger(String[] args){
		/**加载配置文件*/
		BSRConfiger.loadConfiger(args);
	}
	
	/**
	 * BSRServer进行初始化
	 * @throws Exception 
	 */
	public static void initBSRServer() throws Exception{
		/**对缓冲池进行初始化*/
		BSRPoolsHolder.initBSRByteBufferPool();
		/**对JMX监控服务进行初始化*/
		BSRJMXAgent.initBSRJMXAgent();
	}
	
	/**
	 * 初始化选择器
	 * @return
	 * @throws IOException 
	 */
	public static void initSelector() throws IOException{
		/**初始化选择器*/
		SelectorHolder.initDefaultSelector();
	}
	
	/**
	 * 初始化一个ServerSocketChannel
	 * @param selector
	 * @return
	 * @throws IOException
	 */
	public static void initServerSocketChannel() throws IOException{
		/**初始化默认的ServerSocketChannel*/
		ServerSocketChannelHolder.initDefaultServerSocketChannel();
	}
	
	/**
	 * 启动BOSS线程
	 * @throws InterruptedException 
	 * @throws NumberFormatException 
	 */
	public static void runBossThread() throws NumberFormatException, InterruptedException{
		BossThread.bootstrap(SelectorHolder.getSocketSelector(), ServerSocketChannelHolder.getServerSocketChannel());
		Thread.sleep(Long.parseLong(BSRConfiger.getConfig(BSRConstants.THREAD_SKIPTIME)));
	}
	
	/**
	 * ，生成管道线，生成管道线处理器，启动Worker线程
	 * @throws InterruptedException 
	 * @throws NumberFormatException 
	 */
	public static void runWorkerThread() throws NumberFormatException, InterruptedException{
		/**准备管道线*/
		List<BSRPipe> bsrPipeLine = new ArrayList<BSRPipe>();
		bsrPipeLine.add(new EchoPipeLine());
		/**生成管道线处理器*/
		SimpleBSRPipeProcessor bsrPipeProcessor = new SimpleBSRPipeProcessor(bsrPipeLine);
		/**启动Worker线程*/
		/**启动Worker线程*/
		WorkerThread.bootstrap(bsrPipeProcessor);
		Thread.sleep(Long.parseLong(BSRConfiger.getConfig(BSRConstants.THREAD_SKIPTIME)));
	}
	/**
	 * 启动Background线程
	 * @throws InterruptedException 
	 * @throws NumberFormatException 
	 */
	public static void runBackThread() throws NumberFormatException, InterruptedException{
		BackGroundThread.bootstrap();
		Thread.sleep(Long.parseLong(BSRConfiger.getConfig(BSRConstants.THREAD_SKIPTIME)));
	}
}
