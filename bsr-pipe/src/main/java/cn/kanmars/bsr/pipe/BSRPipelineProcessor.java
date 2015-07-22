package cn.kanmars.bsr.pipe;

import java.util.List;

import cn.kanmars.bsr.server.context.BSRContext;
import cn.kanmars.bsr.server.pipe.AbstracePipelineProcessor;
import cn.kanmars.bsr.server.pipe.BSRPipe;

/**
 * 管道线驱动
 * @author baolong
 *
 */
public class BSRPipelineProcessor extends AbstracePipelineProcessor {
	
	public BSRPipelineProcessor(List<BSRPipe> bsrPipeLine) {
		//对管道线进行赋值
		this.bsrPipeLine = bsrPipeLine;
		//创建管道线
		buildPipeLines();
	}

	/**
	 * 管道线处理
	 * @param bsrEvents		BSR事件处理，参见BSREvent.OP_READ,BSREvent.OP_WRITE,BSREvent.OP_CLOSE
	 * @param bsrContext	BSR上下文
	 * @throws Exception 
	 */
	public void execute(String bsrEvents,Object ...objs) throws Exception{
		//获取第一个管道
		BSRPipe firstPipe = bsrPipeLine.get(0);
		//从第一个管道开始向下执行
		firstPipe.execute(bsrEvents,objs);
	}
	
	
}
