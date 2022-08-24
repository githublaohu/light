package com.lamp.light.cloud;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManage {

	private Map<String/* name or ak */, ThirdPartyConfig> configToName = new ConcurrentHashMap<>();

	private Map<String/* name or ak */, ThirdPartyConfig> configToBusiness = new ConcurrentHashMap<>();

	private Map<String, ThirdPartyConfig> configToManufacturer = new ConcurrentHashMap<>();

	private ThirdPartyConfig defaultConfig;

	public synchronized void setThirdPartyConfig(ThirdPartyConfig thirdPartyConfig) {
		if (Objects.nonNull(thirdPartyConfig.getConfiglName())) {
			configToName.put(thirdPartyConfig.getConfiglName(), thirdPartyConfig);
		}

		if (Objects.nonNull(thirdPartyConfig.getBusiness())) {
			configToBusiness.put(thirdPartyConfig.getBusiness(), thirdPartyConfig);
		}
	}

	public synchronized void setDefault(ThirdPartyConfig thirdPartyConfig) {
		defaultConfig = thirdPartyConfig;
	}

	public synchronized void setManufacturer(ThirdPartyConfig thirdPartyConfig) {
		configToManufacturer.put(thirdPartyConfig.getManufacturer(), thirdPartyConfig);
	}

	public ThirdPartyConfig getConfig(ThirdPartyConfig thirdPartyConfig) {
		ThirdPartyConfig config = null;
		if (Objects.nonNull(thirdPartyConfig.getConfiglName())) {
			config = configToName.get(thirdPartyConfig.getConfiglName());
		}

		if (Objects.nonNull(config) && Objects.nonNull(thirdPartyConfig.getBusiness())) {
			config = configToBusiness.get(thirdPartyConfig.getBusiness());
		}

		if (Objects.nonNull(config) && Objects.nonNull(thirdPartyConfig.getBusiness())) {
			config = configToManufacturer.get(thirdPartyConfig.getBusiness());
		}
		return Objects.nonNull(config) ? config : defaultConfig;
	}
}
