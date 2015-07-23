package cn.kanmars.bsr.server.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;

/**
 * 日志处理类
 * @author baolong
 *
 */
public class Logger {
	
	static{
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File(getLogFile()),false));
			pw.println("--start--");
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String getLogFile(){
		return BSRConfiger.getConfig(BSRConstants.LOG_FILE);
	}
	
	public static void info(String msg){
		println(msg);
	}
	public static void debug(String msg){
		println(msg);
	}
	public static void error(String msg,Exception e){
		println(msg);
		if(e!=null){
			e.printStackTrace();
		}
	}
	
	public static void println(String msg){
		System.out.println(msg);
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File(getLogFile()),true));
			pw.println(msg);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
