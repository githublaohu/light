package org.example.spring.mvc;

import java.util.List;

public interface WebHookConfigOperation {

	/**
	 * 添加配置
	 * @return
	 */
	public Integer insertWebHookConfig(WebHookConfig webHookConfig);
	
	/**
	 * 修改配置
	 * @return
	 */
	public Integer updateWebHookConfig(WebHookConfig webHookConfig);
	
	/**
	 * 删除配置
	 * @return
	 */
	public Integer deleteWebHookConfig(WebHookConfig webHookConfig);
	
	/**
	 * 通过id查询配置
	 * @return
	 */
	public WebHookConfig queryWebHookConfigById(String id);
	
	/**
	 * 通过厂商查询配置
	 */
	public List<WebHookConfig> queryWebHookConfigByManufacturer(String Manufacturer,Integer pageNum, Integer pageSize );
}
