package cn.kanmars.bsr.http.session;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BSRHttpSessionHolder {
	
	/**清理时间*/
	public static long cleanTime = 60*1000L;
	/**过期时间*/
	public static long expireTime = 30* 60 * 1000L;
	
	public static TreeMap<String, BSRHttpSession> treeMap = new TreeMap<String, BSRHttpSession>();
	
	
	
	public static void addABSRHttpSession(BSRHttpSession session){
		treeMap.put(session.getId(), session);
	}
	
	public static BSRHttpSession getBSRHttpSession(String sessionid){
		BSRHttpSession session = treeMap.get(sessionid);
		if(session!=null){
			session.setLastAccessedTime(new Date().getTime());
		}
		return session;
	}
	


	static{
		//每分钟执行一次session清除
		Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(new SessionCleanThread(), 0L, cleanTime, TimeUnit.MILLISECONDS);
	}
	
	public static class SessionCleanThread extends Thread{

		@Override
		public void run() {
			Iterator<BSRHttpSession> iterator = treeMap.values().iterator();
			long nowDate = new Date().getTime();
			while(iterator.hasNext()){
				BSRHttpSession bsrHttpSession = iterator.next();
				if((bsrHttpSession.getLastAccessedTime() + bsrHttpSession.getMaxInactiveInterval()*1000) < nowDate){
					iterator.remove();
				}
			}
		}
		
	}
}
