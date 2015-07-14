package cn.kanmars.bsr.http.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
	
	public static String getGMTStr(){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'",Locale.US);
		return sdf.format(new Date());
	}
	
	public static String getGMTStr(Date d){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'",Locale.US);
		return sdf.format(d);
	}
	
	public static Date getDateFromGMTStr(String str) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'",Locale.US);
		return sdf.parse(str);
	}
	
	public static void main(String[] args) throws ParseException {
		System.out.println(getDateFromGMTStr(getGMTStr()));
	}
}
