package cn.kanmars.bsr.server.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;


public class ThreadRegister {
	public final static ExecutorService bossExecutorService = Executors.newFixedThreadPool(Integer.parseInt(BSRConfiger.getConfig(BSRConstants.BOSS_THREAD_NUMBER)));
	public final static ExecutorService workerExecutorService = Executors.newFixedThreadPool(Integer.parseInt(BSRConfiger.getConfig(BSRConstants.WORKER_THREAD_NUMBER)));
	public final static ExecutorService backgroundExecutorService = Executors.newFixedThreadPool(Integer.parseInt(BSRConfiger.getConfig(BSRConstants.BACK_THREAD_NUMBER)));
}
