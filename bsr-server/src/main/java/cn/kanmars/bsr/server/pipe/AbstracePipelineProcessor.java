package cn.kanmars.bsr.server.pipe;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstracePipelineProcessor {
	
	public List<BSRPipe> bsrPipeLine = new ArrayList<BSRPipe>();
	
	/**
	 * 创建管道线，将BSRPipeLine中的管道链接起来
	 * @param bsrPipeLine
	 */
	public void buildPipeLines(){
		//在初始化管道线的时候，建立管道之间的上下级关系
		for(int i=0;i<bsrPipeLine.size();i++){
			BSRPipe this_ = bsrPipeLine.get(i);
			BSRPipe next_ = null;
			if((i+1)<bsrPipeLine.size()){
				next_ = bsrPipeLine.get(i+1);
			}
			this_.setNext(next_);
			if(next_!=null){
				next_.setPre(this_);
			}
		}
	}
	
	/**
	 * 增加一个管道
	 * @param bsrPipe
	 */
	public void addPipe(BSRPipe bsrPipe){
		//在末尾增加管道
		bsrPipeLine.add(bsrPipe);
		//重建管道线
		buildPipeLines();
	}
	/**
	 * 在指定位置增加管道
	 * @param index	位置
	 * @param bsrPipe	管道
	 */
	public void addPipe(int index,BSRPipe bsrPipe){
		//在指定位置增加管道
		bsrPipeLine.add(index,bsrPipe);
		//重建管道线
		buildPipeLines();
	}
	/**
	 * 获取指定位置的管道
	 * @param index
	 * @return
	 */
	public BSRPipe getPipe(int index){
		//返回指定的管道线
		return bsrPipeLine.get(index);
	}
	
	public abstract void execute(String bsrEvents,Object ... objs);
}
