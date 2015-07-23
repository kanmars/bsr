package cn.kanmars.bsr.server.thread.boss;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;

import cn.kanmars.bsr.server.context.BSRContext;
import cn.kanmars.bsr.server.context.BSRContextRegister;
import cn.kanmars.bsr.server.log.Logger;
import cn.kanmars.bsr.server.thread.ThreadRegister;

/**
 * 分发器线程
 * @author baolong
 *
 */
public class BossThread extends Thread {
	
	public String name = ""+new Random().nextInt();
	
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;

	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public ServerSocketChannel getServerSocketChannel() {
		return serverSocketChannel;
	}

	public void setServerSocketChannel(ServerSocketChannel serverSocketChannel) {
		this.serverSocketChannel = serverSocketChannel;
	}

	public BossThread(Selector selector, ServerSocketChannel serverSocketChannel) {
		super();
		this.selector = selector;
		this.serverSocketChannel = serverSocketChannel;
	}

	public void run() {
		Logger.info("BossThread开始运行");
		while (true) {
			try {
				if (selector.select(10000) == 0) {
					//Logger.debug("等待....");
					continue;
				}
				Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
				//System.out.println("线程["+name+"] size = "+selector.selectedKeys().size());
				while(ite.hasNext()){
					try{
						SelectionKey key = ite.next();
						//选择出后，立即删除该键
						ite.remove();
						//当客户端有连接之后，创建BSRContext并且注册入全局注册表中
						if(key.isAcceptable()){
							Logger.debug("客户端发起链接..");
							ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
							//将socketChannel注册到选择器中
							SocketChannel socketChannel = serverSocketChannel.accept();
							socketChannel.configureBlocking(false);
							socketChannel.register(selector, SelectionKey.OP_READ);
							//构建上下文
							BSRContext bsrContext = new BSRContext();
							bsrContext.setSelector(selector);
							bsrContext.setSocketChannel(socketChannel);
							bsrContext.setCreateTime(System.currentTimeMillis());
							bsrContext.setOperationTime(System.currentTimeMillis());
							bsrContext.changeBSRContext2Read();
							bsrContext.setLive(true);
							Logger.debug("客户端发起链接UUID["+bsrContext.getUuid()+"]");
							boolean result = BSRContextRegister.registerBSRContext(socketChannel,bsrContext);
							if(!result){
								try{
									BSRContextRegister.removeBSRContext(socketChannel);
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}else if(key.isReadable()){
							//当客户端链接可读之后
							Logger.debug("客户端可读操作..");
							SocketChannel socketChannel = (SocketChannel)key.channel();
							BSRContext bsrContext = BSRContextRegister.getBSRContext(socketChannel);
							boolean addResult = BSRContextRegister.addOperableBSRContext(bsrContext);
							Logger.debug("客户端可读UUID["+bsrContext.getUuid()+"]分发["+addResult+"]");
						}else if(key.isWritable()){
							//当客户端链接可读之后
							Logger.debug("客户端可写操作..");
							SocketChannel socketChannel = (SocketChannel)key.channel();
							BSRContext bsrContext = BSRContextRegister.getBSRContext(socketChannel);
							boolean addResult = BSRContextRegister.addOperableBSRContext(bsrContext);
							Logger.debug("客户端可写UUID["+bsrContext.getUuid()+"]分发["+addResult+"]");
						}
					}catch(Exception e){
						//Logger.error("发生了异常", e);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startup(){
		ThreadRegister.bossExecutorService.execute(this);
	}

}
