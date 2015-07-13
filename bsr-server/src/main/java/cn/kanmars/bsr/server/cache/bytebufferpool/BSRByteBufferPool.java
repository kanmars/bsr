package cn.kanmars.bsr.server.cache.bytebufferpool;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.kanmars.bsr.server.cache.BSRPool;
import cn.kanmars.bsr.server.log.Logger;

/**
 * BSRByteBufferPool 用于获取ByteBuffer对象
 * 
 * @author baolong
 *
 */
public class BSRByteBufferPool implements BSRPool<ByteBuffer> {

	LinkedBlockingQueue<ByteBuffer> linkedBlockingQueue = null;

	/**
	 * 初始化参数 参数列表如下： Integer number块总数 Integer size每块的块大小
	 */
	public void initBSRPool(Object... objs) {
		Integer number = (Integer) objs[0];
		Integer size = (Integer) objs[1];
		linkedBlockingQueue = new LinkedBlockingQueue<ByteBuffer>(number);
		for (int i = 0; i < number; i++) {
			ByteBuffer tmp = ByteBuffer.allocate(size);
			linkedBlockingQueue.add(tmp);
		}
	}

	/**
	 * 尝试获取一个ByteBuffer，超时时间为默认的最大，有可能返回空
	 */
	public ByteBuffer requireT() {
		try {
			ByteBuffer byteBuffer = linkedBlockingQueue.poll(1, TimeUnit.DAYS);
			byteBuffer.clear();
			return byteBuffer;
		} catch (InterruptedException e) {
			Logger.error("尝试获取ByteBuffer发生异常", e);
		}
		return null;
	}

	/**
	 * 尝试获取一个ByteBuffer，超时时间为timeout毫秒，有可能返回空
	 */
	public ByteBuffer requireT(long timeout) {
		try {
			ByteBuffer byteBuffer = linkedBlockingQueue.poll(timeout,
					TimeUnit.MILLISECONDS);
			byteBuffer.clear();
			return byteBuffer;
		} catch (InterruptedException e) {
			Logger.error("尝试获取ByteBuffer发生异常", e);
		}
		return null;
	}

	public void releaseT(ByteBuffer byteBuffer) {
		try {
			linkedBlockingQueue.put(byteBuffer);
		} catch (InterruptedException e) {
			Logger.error("尝试释放ByteBuffer发生异常", e);
		}
	}

}
