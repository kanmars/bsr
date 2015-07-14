package cn.kanmars.bsr.server.thread.boss;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import cn.kanmars.bsr.server.context.BSRContext;
import cn.kanmars.bsr.server.context.BSRContextRegister;
import cn.kanmars.bsr.server.log.Logger;

/**
 * 分发器线程
 * @author baolong
 *
 */
public class BossThread extends Thread {
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
				while(ite.hasNext()){
					try{
						SelectionKey key = ite.next();
						//选择出后，立即删除该键
						ite.remove();
						//当客户端有连接之后，创建BSRContext并且注册入全局注册表中
						try{
							if(key.isAcceptable()){
								Logger.info("客户端发起链接..");
								ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
								//将socketChannel注册到选择器中
								SocketChannel socketChannel = serverSocketChannel.accept();
								socketChannel.configureBlocking(false);
								socketChannel.register(selector, SelectionKey.OP_READ);
								//构建上下文
								BSRContext bsrContext = new BSRContext();
								bsrContext.setSocketChannel(socketChannel);
								bsrContext.setCreateTime(System.currentTimeMillis());
								bsrContext.setOperationTime(System.currentTimeMillis());
								bsrContext.setLive(true);
								
								boolean result = BSRContextRegister.registerBSRContext(socketChannel,bsrContext);
								if(!result){
									try{
										BSRContextRegister.removeBSRContext(socketChannel);
									}catch(Exception e){
										e.printStackTrace();
									}
								}
							}
							//当客户端链接可读之后
							if(key.isReadable()){
								Logger.debug("客户端消息输入..");
								SocketChannel socketChannel = (SocketChannel)key.channel();
								BSRContext bsrContext = BSRContextRegister.getBSRContext(socketChannel);
								boolean addResult = BSRContextRegister.addReadableBSRContext(bsrContext);
								if(addResult){
									key.interestOps(SelectionKey.OP_READ);
								}
							}
						}catch(Exception e){
							key.cancel();
						}
					}catch(Exception e){
						try{
							ite.remove();
						}catch(Exception e2){
							//	e2.printStackTrace();
						}
						Logger.error("发生了异常", e);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
