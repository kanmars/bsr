package cn.kanmars.bsr.server.config;

import java.util.HashMap;
import java.util.Map;

import cn.kanmars.bsr.server.constant.BSRConstants;

/**
 * BSR服务器配置文件处理类
 * @author baolong
 *
 */
public class BSRConfiger {
	
	/**
	 * 配置文件保存对象
	 */
	private static Map<String,String> configs = new HashMap<String, String>();
	
	/**
	 * 加载服务器配置
	 * @param args
	 */
	public static void loadConfiger(String[] args){
		/**加载默认的configs*/
		initDefaultConfigs();
		/**解析参数中的配置文件*/
		parseArgsConfigs(args);
	}

	/**
	 * 加载默认的configs
	 */
	public static void initDefaultConfigs(){
		/**绑定IP*/
		configs.put(BSRConstants.BIND_IP, "0.0.0.0");
		/**绑定端口*/
		configs.put(BSRConstants.BIND_PORT, "1234");
		/**JMX端口*/
		configs.put(BSRConstants.JMX_PORT, "1235");
		/**操作的超时时间*/
		configs.put(BSRConstants.TIMEOUT, "60000");
		/**单个链接的最大超时时间*/
		configs.put(BSRConstants.MAXCONNTIME, "120000");
		/**BACKLOG队列长度*/
		configs.put(BSRConstants.BACKLOG, "1024000");
		/**允许同时建立链接的数量*/
		configs.put(BSRConstants.MAXCONNECTIONS, "65536");
		/**进程启动的延时时间*/
		configs.put(BSRConstants.THREAD_SKIPTIME, "300");
		/**boss线程数量*/
		configs.put(BSRConstants.BOSS_THREAD_NUMBER, "3");
		/**worker线程数量*/
		configs.put(BSRConstants.WORKER_THREAD_NUMBER, "10");
		/**后台清理线程数量*/
		configs.put(BSRConstants.BACK_THREAD_NUMBER, "2");
		/**缓冲区池的个数*/
		configs.put(BSRConstants.BYTEBUFFER_NUMBER, "1000");
		/**缓冲区池中缓冲对象每个的大小*/
		configs.put(BSRConstants.BYTEBUFFER_SIZE, "1024");
		/**缓冲区池中缓冲对象每个的大小*/
		configs.put(BSRConstants.LOG_FILE, "E:/1log.txt");
		/**静态资源的ROOT路径*/
		configs.put(BSRConstants.ROOT_DIR, "E:/kanmars.github.com/_site/");
		
	}
	
	/**
	 * 解析参数中的配置文件
	 */
	public static void parseArgsConfigs(String[] args){
		/**解析配置文件并覆盖原有参数*/
		for(String s:args){
			/**如果带等号，并且不以-开头，则认为是配置参数，否则忽略*/
			if(s.indexOf("=")>0 && !s.startsWith("-")){
				String key = s.substring(0,s.indexOf("="));
				String value = s.substring(s.indexOf("=")+1);
				configs.put(key.toUpperCase(), value);
			}
		}
	}
	
	/**
	 * 获取配置属性
	 * @param configName
	 * @return
	 */
	public static String getConfig(String configName){
		/** 如果在配置文件保存对象中已经有了该值，则直接返回 */
		String config_value = configs.get(configName.toUpperCase());
		return config_value;
	}
}
