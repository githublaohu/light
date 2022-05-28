package org.example.spring.mvc;

import java.util.Map;

public interface ManufacturerProtocol {

	/**
	 * 如果认证识别，或则协议解析失败，请抛出异常
	 * @param webHookRequest
	 * @param header
	 * @return
	 */
	public void execute(WebHookRequest webHookRequest,WebHookConfig webHookConfig,Map<String, String> header);
}
