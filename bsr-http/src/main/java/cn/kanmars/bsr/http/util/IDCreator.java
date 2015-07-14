package cn.kanmars.bsr.http.util;

import java.util.Date;
import java.util.Random;

public class IDCreator {
	
	private static Random random = new Random();
	
	public static String createId(String pre,int length){
		return createId(pre,8,length);//默认的日期长度为8
	}
	
	public static String createId(String pre,int datelength,int length){
		
		StringBuffer sb = new StringBuffer();
		sb.append(pre);
		//时间戳从2015年1月1日开始计算
		sb.append(StringUtils.fillString(LongTransfer.getStrFrom(new Date().getTime()-new Date(115,0,1).getTime()), '0', datelength, false));
		int nowLength = sb.length();
		if(nowLength >= length ){
			return sb.toString();
		}
		int random_length = length - nowLength;
		if(random_length>10)random_length=10;//限制随机数长度
		int random_int = random.nextInt((int)(Math.pow(10, random_length)-1));
		String suff = ""+random_int;
		
		suff = StringUtils.fillString(suff,'0',length-nowLength,false);
		sb.append(suff);
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(createId("A",10));
		System.out.println(IDCreator.createId("BSR", 30));
		System.out.println(LongTransfer.getStrFrom(new Date().getTime()-new Date(115,0,1).getTime()));
	}
}
