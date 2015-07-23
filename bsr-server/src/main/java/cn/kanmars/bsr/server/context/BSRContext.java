package cn.kanmars.bsr.server.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

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
	 * 选择器
	 */
	private Selector selector;
	
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
	 * 读写标志
	 */
	private String read_write_flg ;
	
	/**
	 * 是否存活
	 */
	private volatile boolean isLive;

	/**
	 * 唯一ID
	 */
	private UUID uuid;
	/**
	 * 读取到的内容
	 */
	private ByteArrayOutputStream readBao = new ByteArrayOutputStream();
	/**
	 * 写出的内容
	 */
	private ByteArrayOutputStream writeBao = new ByteArrayOutputStream();

	/**
	 * 可重入的非公平锁，线程获取锁顺序与请求顺序无关
	 */
	private ReentrantLock lock = new ReentrantLock();
	
	/**
	 * 当前上下文的占有者
	 */
	private volatile Object owner = null;
	
	public BSRContext() {
		uuid = UUID.randomUUID();
	}

	/**
	 * 获取请求字节内容
	 * @return
	 */
	public byte[] getReadContent(){
		return readBao.toByteArray();
	}
	
	/**
	 * 获取输出字节内容
	 * @return
	 */
	public byte[] getWriteContent(){
		return writeBao.toByteArray();
	}
	
	/**
	 * 清空内容
	 */
	public void cleanContent(){
		readBao.reset();
		writeBao.reset();
	}
	
	/**
	 * 向BSRContext中输出一个字节数组
	 * @param bytes
	 */
	public void write(byte[] bytes){
		if(isLive()){
			Logger.info("客户端["+getUuid()+"]尝试输出["+new String(bytes,0,bytes.length>1000?1000:bytes.length) +"]");
			//从缓冲池中获取ByteBuffer
			ByteBuffer byteBuffer = BSRPoolsHolder.getBSRByteBufferPool().requireT();
			int out = 0;
			int all_length = bytes.length;
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
						out += socketChannel.write(byteBuffer);
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
	
	/**
	 * 变更选择器监听时间为可写
	 * @throws ClosedChannelException
	 */
	public void changeSelectorRegister2Write() throws ClosedChannelException{
		this.socketChannel.register(selector, SelectionKey.OP_WRITE);
		changeBSRContext2Write();
	}
	/**
	 * 变更选择器监听时间为可读
	 * @throws ClosedChannelException
	 */
	public void changeSelectorRegister2Read() throws ClosedChannelException{
		this.socketChannel.register(selector, SelectionKey.OP_READ);
		changeBSRContext2Read();
	}
	
	/**
	 * 修改读写标志为读
	 */
	public void changeBSRContext2Read(){
		this.read_write_flg = BSRConstants.BSRContext_READ_FLG;
	}
	/**
	 * 修改读写标志为写
	 */
	public void changeBSRContext2Write(){
		this.read_write_flg = BSRConstants.BSRContext_WRITE_FLG;
	}
	
	/**
	 * 判断当前上下文是否是读状态
	 * @return
	 */
	public boolean isRead(){
		if(this.read_write_flg!=null && this.read_write_flg.equals(BSRConstants.BSRContext_READ_FLG)){
			return true;
		}
		return false;
	}
	/**
	 * 判断当前上下文是否是写状态
	 * @return
	 */
	public boolean isWrite(){
		if(this.read_write_flg!=null && this.read_write_flg.equals(BSRConstants.BSRContext_WRITE_FLG)){
			return true;
		}
		return false;
	}
	
	/**
	 * 改变当前上下文的所有者，如果该上下文已被其他人拥有，则返回false
	 * @param o
	 * @return
	 */
	public boolean changeOwner(Object o){
		lock.lock();
		boolean result = false;
		if(owner!=null){
			result =  false;
		}else{
			owner = o;
			result = true;
		}
		lock.unlock();
		return result;
	}
	/**
	 * 清除上下文的所有者，如果清除者不是拥有者，则返回false
	 * @param o
	 * @return
	 */
	public boolean cleanOwner(Object o){
		lock.lock();
		boolean result = false;
		if(owner!=null && owner!=o){
			result =  false;
		}else{
			owner = null;
			result = true;
		}
		lock.unlock();
		return result;
	}
	/**
	 * 判断当前上下文的所有者是否是o
	 * @param o
	 * @return
	 */
	public boolean isOwner(Object o){
		lock.lock();
		boolean result = false;
		if(owner!=null && owner==o){
			result =  true;
		}else{
			result = false;
		}
		lock.unlock();
		return result;
	}
	
	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
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

	public String getRead_write_flg() {
		return read_write_flg;
	}

	public void setRead_write_flg(String read_write_flg) {
		this.read_write_flg = read_write_flg;
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
	
	
	public ByteArrayOutputStream getReadBao() {
		return readBao;
	}

	public void setReadBao(ByteArrayOutputStream readBao) {
		this.readBao = readBao;
	}

	public ByteArrayOutputStream getWriteBao() {
		return writeBao;
	}

	public void setWriteBao(ByteArrayOutputStream writeBao) {
		this.writeBao = writeBao;
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
