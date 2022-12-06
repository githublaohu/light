package com.lamp.light.api.request;

import java.util.Map;

public class CurrencyRequest {

	private String address;
	
	private String port;
	
	private String url;
	
	private String requestMethod;
	
	private Map<String,Object> header;
	
	private Map<String,String> query;
	
	private Map<String,String> form;
	
	private byte[] body;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public Map<String, Object> getHeader() {
		return header;
	}

	public void setHeader(Map<String, Object> header) {
		this.header = header;
	}

	public Map<String, String> getQuery() {
		return query;
	}

	public void setQuery(Map<String, String> query) {
		this.query = query;
	}

	public Map<String, String> getForm() {
		return form;
	}

	public void setForm(Map<String, String> form) {
		this.form = form;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
	
}
