package cn.kanmars.bsr.server.log;

import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;

/**
 * 日志处理类
 * @author baolong
 *
 */
public class Logger {
	
	public static String getLogFile(){
		return BSRConfiger.getConfig(BSRConstants.LOG_FILE);
	}
	
	public static void info(String msg){
		System.out.println(msg);
	}
	public static void debug(String msg){
		System.out.println(msg);
	}
	public static void error(String msg,Exception e){
		System.out.println(msg);
		if(e!=null){
			e.printStackTrace();
		}
	}
}
