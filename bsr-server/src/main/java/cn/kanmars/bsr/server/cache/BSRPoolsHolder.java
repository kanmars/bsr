package cn.kanmars.bsr.server.cache;

import cn.kanmars.bsr.server.cache.bytebufferpool.BSRByteBufferPool;
import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;

public class BSRPoolsHolder {

	private static BSRByteBufferPool bsrByteBufferPool = null;
	
	/**
	 * 获得BSRByteBufferPool的缓存类
	 * 
	 * @return
	 */
	public static void initBSRByteBufferPool() {
		if (bsrByteBufferPool == null) {
			bsrByteBufferPool = new BSRByteBufferPool();
			bsrByteBufferPool.initBSRPool(
					Integer.parseInt(BSRConfiger.getConfig(BSRConstants.BYTEBUFFER_NUMBER)), 
					Integer.parseInt(BSRConfiger.getConfig(BSRConstants.BYTEBUFFER_SIZE))
			);
		}
	}
	
	/**
	 * 获取一个BSRByteBufferPool
	 * @return
	 */
	public static BSRByteBufferPool getBSRByteBufferPool(){
		return bsrByteBufferPool;
	}
}
