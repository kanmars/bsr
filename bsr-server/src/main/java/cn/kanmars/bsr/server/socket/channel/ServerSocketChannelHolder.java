package cn.kanmars.bsr.server.socket.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;
import cn.kanmars.bsr.server.socket.selector.SelectorHolder;

/**
 * ServerSocketChannel保存类
 * @author baolong
 *
 */
public class ServerSocketChannelHolder {

	private static ServerSocketChannel ssc = null;
	
	/**
	 * 初始化默认的ServerSocketChannel
	 * @throws IOException
	 */
	public static void initDefaultServerSocketChannel() throws IOException{
		if(ssc == null){
			ServerSocketChannel ssc_ = ServerSocketChannel.open();
			ssc_.socket().bind(
					new InetSocketAddress(
							BSRConfiger.getConfig(BSRConstants.BIND_IP),
							Integer.parseInt(BSRConfiger.getConfig(BSRConstants.BIND_PORT))
							), 
							Integer.parseInt(BSRConfiger.getConfig(BSRConstants.BACKLOG)));
			ssc_.configureBlocking(false);
			ssc_.register(SelectorHolder.getSocketSelector(), SelectionKey.OP_ACCEPT);
			ssc = ssc_;
		}
	}
	
	/**
	 * 获得一个ServerSocketChannel通道
	 * @return
	 */
	public static ServerSocketChannel getServerSocketChannel(){
		return ssc;
	}
}
