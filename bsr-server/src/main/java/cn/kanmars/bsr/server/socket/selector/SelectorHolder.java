package cn.kanmars.bsr.server.socket.selector;

import java.io.IOException;
import java.nio.channels.Selector;

/**
 * 选择器保存类
 * @author baolong
 *
 */
public class SelectorHolder {

	private static Selector selector = null;
	
	/**
	 * 初始化选择器
	 * @throws IOException
	 */
	public static void initDefaultSelector() throws IOException{
		if(selector == null){
			selector = Selector.open();
		}
	}
	
	/**
	 * 获得一个Selector选择器
	 * @return
	 */
	public static Selector getSocketSelector(){
		return selector;
	}
}
