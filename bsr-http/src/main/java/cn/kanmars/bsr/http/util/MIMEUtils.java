package cn.kanmars.bsr.http.util;


public class MIMEUtils {
	/**
	 * 从后缀名，获取MIME类型
	 * @param fileSuffix	例如:.jpg   .png等
	 * @return
	 */
	public static String getMIME(String fileSuffix){
		return PropertiesUtils.getString("mime/mime.properties", fileSuffix);
	}
	
	public static void main(String[] args) {
		System.out.println(getMIME(".jpg"));
	}
	
}
