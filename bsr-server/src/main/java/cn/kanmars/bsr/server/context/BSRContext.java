package cn.kanmars.bsr.server.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import cn.kanmars.bsr.server.cache.BSRPoolsHolder;
import cn.kanmars.bsr.server.cache.bytebufferpool.BSRByteBufferPool;
import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;
import cn.kanmars.bsr.server.log.Logger;

/**
 * BSR处理上下文
 * @author baolong
 *
 */
public class BSRContext implements Comparable<BSRContext> {
	/**
	 * socketChannel上下文
	 */
	private SocketChannel socketChannel;
	
	/**
	 * 创建时间
	 */
	private long createTime;
	
	/**
	 * 上次操作时间
	 */
	private long operationTime;
	
	/**
	 * 是否存活
	 */
	private volatile boolean isLive;

	/**
	 * 唯一ID
	 */
	private UUID uuid;
	/**
	 * 连接内容
	 */
	private ByteArrayOutputStream bao = new ByteArrayOutputStream();

	public BSRContext() {
		uuid = UUID.randomUUID();
	}

	/**
	 * 获取字节内容
	 * @return
	 */
	public byte[] getContent(){
		return bao.toByteArray();
	}
	
	/**
	 * 清空内容
	 */
	public void cleanContent(){
		bao.reset();
	}
	
	/**
	 * 向BSRContext中输出一个字节数组
	 * @param bytes
	 */
	public void write(byte[] bytes){
		if(isLive()){
			//从缓冲池中获取ByteBuffer
			ByteBuffer byteBuffer = BSRPoolsHolder.getBSRByteBufferPool().requireT();
			try {
				int size = Integer.parseInt(BSRConfiger.getConfig(BSRConstants.BYTEBUFFER_SIZE));
				for(int i=0;i<bytes.length;i+=size){
					byteBuffer.clear();
					int length = size;
					//如果I下标+size，后移size位，bytes.length-1下标
					if(i+size > bytes.length-1){
						//则长度等于(bytes.length -1) -(i-11)的下标
						//length = (bytes.length-1) - (i-1);
						//化简后的结果
						length = bytes.length-i;
					}
					byteBuffer.put(bytes,i,length);
					byteBuffer.flip();
					while(byteBuffer.hasRemaining()){
						socketChannel.write(byteBuffer);
					}
				}
			} catch (IOException e) {
				Logger.error("客户端已经关闭，无法写出", e);
			}finally{
				//将byteBuffer放回缓冲池
				BSRPoolsHolder.getBSRByteBufferPool().releaseT(byteBuffer);
			}
		}
	}
	
	/**
	 * 关闭方法
	 */
	public void close(){
		if(socketChannel!=null){
			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(long operationTime) {
		this.operationTime = operationTime;
	}

	public boolean isLive() {
		return socketChannel.isConnected()&& isLive;
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}
	
	public ByteArrayOutputStream getBao() {
		return bao;
	}

	public void setBao(ByteArrayOutputStream bao) {
		this.bao = bao;
	}
	
	/**
	 * 用于ConcurrentSkipListSet的比较
	 */
	public int compareTo(BSRContext o) {
		return uuid.compareTo(o.getUuid());
	}

	public int hashCode() {
		return uuid.hashCode();
	}
	/**
	 * 用于Map的比较
	 */
	public boolean equals(Object obj) {
		if(!(obj instanceof BSRContext)){
			return false;
		}
		return uuid.equals(((BSRContext)obj).getUuid());
	}
	
	
	
}
