package cn.kanmars.bsr.pipe.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import cn.kanmars.bsr.http.request.BSRHttpServletRequest;
import cn.kanmars.bsr.http.response.BSRHttpServletResponse;
import cn.kanmars.bsr.http.util.StringUtils;
import cn.kanmars.bsr.server.cache.bytecache.BSRByteCache;
import cn.kanmars.bsr.server.config.BSRConfiger;
import cn.kanmars.bsr.server.constant.BSRConstants;
import cn.kanmars.bsr.server.event.BSREvents;
import cn.kanmars.bsr.server.pipe.BSRPipe;

/**
 * 静态资源请求管道
 * @author baolong
 *
 */
public class HttpResourcePipe extends BSRPipe {
	
	private String rootDir = "";
	
	
	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}


	@Override
	public void execute(String bsrEvents, Object... obj) {
		if(bsrEvents.equals(BSREvents.OP_READ)){
			BSRHttpServletRequest bsrHttpServletRequest = (BSRHttpServletRequest)obj[0];
			BSRHttpServletResponse bsrHttpServletResponse = (BSRHttpServletResponse)obj[1];
			
			String reqURI = bsrHttpServletRequest.getRequestURI();
			
			reqURI = URLDecoder.decode(reqURI);
			String resourcePath = reqURI;
			if(resourcePath.indexOf("?")>0){
				resourcePath = resourcePath.substring(0,resourcePath.indexOf("?"));
			}
			
			if(StringUtils.isEmpty(rootDir)){
				rootDir = BSRConfiger.getConfig(BSRConstants.ROOT_DIR);
			}
			
			String realPath = null;
			if(resourcePath.startsWith(File.separator)){
				realPath = rootDir + resourcePath;
			}else{
				realPath = rootDir + File.separator + resourcePath;
			}
			
			byte [] data = BSRByteCache.getBSRByteCacheData(realPath);
			
			if(data!=null){
				try {
					bsrHttpServletResponse.getOutputStream().write(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//有缓存数据，完成处理，不后续操作
			}else  {
				File f = new File(realPath);
				if(f.exists() && f.isFile()){
					//如果该静态资源存在并且是文件
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					try {
						InputStream in = new FileInputStream(f);
						byte[] tmp = new byte[1024];
						int count = 0;
						while((count = in.read(tmp))>=0){
							bao.write(tmp,0,count);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					data = bao.toByteArray();
					BSRByteCache.addBSRBytes(realPath, data);
					
					try {
						bsrHttpServletResponse.getOutputStream().write(data);
						//数据处理完成后,跳出该任务
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}else{
					//没有找到静态资源，进行下一步操作
					doNext(bsrEvents, obj);
				}
			}
			
			
			
			
		}
	}
}
