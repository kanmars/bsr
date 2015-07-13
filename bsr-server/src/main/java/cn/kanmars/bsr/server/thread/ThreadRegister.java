package cn.kanmars.bsr.server.thread;

import java.util.concurrent.ConcurrentHashMap;

public class ThreadRegister {
	private static ConcurrentHashMap<String, Thread> threadRegister = new ConcurrentHashMap<String, Thread>();
	
	public synchronized static Thread getThread(String threadName){
		return threadRegister.get(threadName);
	}
	public synchronized static void putThread(String threadName,Thread thread){
		threadRegister.put(threadName, thread);
	} 
}
