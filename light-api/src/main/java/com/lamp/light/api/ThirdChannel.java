package com.lamp.light.api;

/**
 * 
 * ThirdChannel <BR>
 * 
 * @author laohu
 */
public abstract class ThirdChannel {

	private String id;

	// 团队
	private String team;

	// 业务线
	private String business;

	// 阿里云，等等
	private String manufacturer;

	// 使用那个配置
	private String configlName;

	private String bucket;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getConfiglName() {
		return configlName;
	}

	public void setConfiglName(String configlName) {
		this.configlName = configlName;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

}
