package cn.kanmars.bsr.server.pipe;

import java.util.List;

import cn.kanmars.bsr.server.context.BSRContext;

/**
 * 管道线驱动
 * @author baolong
 *
 */
public class SimpleBSRPipeProcessor extends AbstracePipelineProcessor {
	
	public SimpleBSRPipeProcessor(List<BSRPipe> bsrPipeLine) {
		//对管道线进行赋值
		this.bsrPipeLine = bsrPipeLine;
		//创建管道线
		buildPipeLines();
	}

	/**
	 * 管道线处理
	 * @param bsrEvents		BSR事件处理，参见BSREvent.OP_READ,BSREvent.OP_WRITE,BSREvent.OP_CLOSE
	 * @param bsrContext	BSR上下文
	 */
	public void execute(String bsrEvents,Object ... objs){
		//获取第一个管道
		BSRPipe firstPipe = bsrPipeLine.get(0);
		//从第一个管道开始向下执行
		firstPipe.execute(bsrEvents,objs);
	}
	
}
