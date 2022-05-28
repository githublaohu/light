package org.example.spring.mvc;

public class WebHookRequest {

	/**
	 * 厂商事件id
	 */
	private String manufacturerEvnetId;
	
	/**
	 * 厂商事件名
	 */
	private String manfacturerEvnetName;
	
	/**
	 * 厂商名
	 */
	private String manufacturerSource;
	
	/**
	 * 数据
	 */
	private byte[] data;
}
