package cn.kanmars.bsr.server.cache.bytecache;

import java.util.Date;
import java.util.TreeMap;

import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;

/**
 * BSR的缓存池
 * @author baolong
 *
 */
public class BSRByteCache {
	
	public static class BSRByteCacheData{
		
		String key = null;
		
		byte[] data = null;
		
		long createtime = 0;
		
		long lastRequestTime = 0;
		
		long expiretime = 0;
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public byte[] getData() {
			return data;
		}
		public void setData(byte[] data) {
			this.data = data;
		}
		public long getCreatetime() {
			return createtime;
		}
		public void setCreatetime(long createtime) {
			this.createtime = createtime;
		}
		public long getLastRequestTime() {
			return lastRequestTime;
		}
		public void setLastRequestTime(long lastRequestTime) {
			this.lastRequestTime = lastRequestTime;
		}
		public long getExpiretime() {
			return expiretime;
		}
		public void setExpiretime(long expiretime) {
			this.expiretime = expiretime;
		}
		
		
	}
	
	public static TreeMap<String, BSRByteCacheData> treeMap = new TreeMap<String,BSRByteCacheData>();
	
	public static byte[] getBSRByteCacheData(String uri){
		BSRByteCacheData bsrByteCacheData = treeMap.get(uri);
		if(bsrByteCacheData==null){
			//不存在
			return null;
		}
		if(bsrByteCacheData.getLastRequestTime()+bsrByteCacheData.getExpiretime() < new Date().getTime()){
			//已过期
			treeMap.remove(uri);
			return null;
		}
		long t = new Date().getTime();
		bsrByteCacheData.setLastRequestTime(t);
		return bsrByteCacheData.getData();
	}
	
	public static void addBSRBytes(String uri,byte[] data){
		BSRByteCacheData bsrByteCacheData = new BSRByteCacheData();
		
		bsrByteCacheData.setKey(uri);
		bsrByteCacheData.setData(data);
		long t = new Date().getTime();
		bsrByteCacheData.setCreatetime(t);
		bsrByteCacheData.setLastRequestTime(t);
		bsrByteCacheData.setExpiretime(Integer.parseInt(BSRConfiger.getConfig(BSRConstants.BYTECACHE_EXPIRESECOND))*1000);//N秒过期
		treeMap.put(uri, bsrByteCacheData);
	}
}
