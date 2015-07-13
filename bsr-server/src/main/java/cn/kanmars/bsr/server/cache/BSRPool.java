package cn.kanmars.bsr.server.cache;

/**
 * 池化接口
 * @author baolong
 *
 * @param <T>
 */
public interface  BSRPool<T> {
	
	public void initBSRPool(Object... objs);
	
	/**
	 * 申请一个T资源，如果没有申请到则永远等待
	 * @return
	 */
	public T requireT();
	
	/**
	 * 申请一个T资源，超时时间为参数所设置，单位为毫秒
	 * @param timeout
	 * @return
	 */
	public T requireT(long timeout);
	
	/**
	 * 释放一个资源
	 */
	public void releaseT(T t);
	
}
