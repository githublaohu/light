package org.example.spring.mvc;

import java.util.Map;

public interface WebHookController {

	/**
	 * 1. 通过path获得webhookConfig
	 * 2. 获得对应的厂商的处理对象,并解析协议
	 * 3. 通过WebHookConfig获得cloudEvnet 协议对象
	 * 4. WebHookRequest 转换为cloudEvent。
	 * @param path
	 * @param header 需要把请求头信息重写到map里面
	 * @param body
	 */
	public void execute(String path,Map<String,String> header,byte[] body);
}
