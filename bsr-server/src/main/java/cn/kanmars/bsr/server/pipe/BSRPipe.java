package cn.kanmars.bsr.server.pipe;


public abstract class BSRPipe {
	
	/** 前一个管道对象*/
	public BSRPipe pre;
	/** 下一个管道对象*/
	public BSRPipe next;
	
	/**
	 * 执行本方法
	 * @param obj
	 * @param bsrEvents
	 */
	public abstract void execute(Object obj,String bsrEvents);
	
	/**
	 * 执行下一个方法
	 * @param obj
	 * @param bsrEvents
	 */
	public void doNext(Object obj,String bsrEvents){
		if(next != null){
			next.execute(obj, bsrEvents);
		}
	}

	public BSRPipe getPre() {
		return pre;
	}

	public void setPre(BSRPipe pre) {
		this.pre = pre;
	}

	public BSRPipe getNext() {
		return next;
	}

	public void setNext(BSRPipe next) {
		this.next = next;
	}
	
	
}
