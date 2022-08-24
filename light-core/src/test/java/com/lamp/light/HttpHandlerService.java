package com.lamp.light;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class HttpHandlerService {

	private Map<String , HttpProcessor> httpProcessorMap = new ConcurrentHashMap<>();
	
	public void register(HttpProcessor httpProcessor, ThreadPoolExecutor threadPoolExecutor) {
		for(String path : httpProcessor.paths()) {
			if( httpProcessorMap.containsKey(path)) {
				// 冲突
			}
			// TODO 是否检查前缀冲突。 /a/b 与 /a/b/c 。 两者已经冲突了
			httpProcessorMap.put(path, httpProcessor);
		}
	}
	
	/**
	 * handler对请求进行统筹管理，把基本能力都在handler里面处理完
	 * 所有能力封装在HttpHandlerService里面
	 * 不透传任何能力到HttpProcessor里面
	 * @param httpRequest
	 */
	public void handler(ChannelHandlerContext ctx,HttpRequest httpRequest) {
		// http 基本能力
		
		// metrics 能力
		
		// 日志能力
		
		// trace
		
		// 异步调动
	}
}
