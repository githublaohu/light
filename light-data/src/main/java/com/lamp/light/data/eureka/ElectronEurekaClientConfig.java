/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PubL v2.
 *You can use this software according to the terms and conditions of the Mulan PubL v2.
 *You may obtain a copy of Mulan PubL v2 at:
 *         http://license.coscl.org.cn/MulanPubL-2.0
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PubL v2 for more details.
 */
package com.lamp.light.data.eureka;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netflix.appinfo.EurekaAccept;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;

public class ElectronEurekaClientConfig implements EurekaClientConfig{

	public static final String DEFAULT_ZONE = "defaultZone";

	private static final int MINUTES = 60;


	private boolean enabled = true;


	private int registryFetchIntervalSeconds = 30;

	private int instanceInfoReplicationIntervalSeconds = 30;

	private int initialInstanceInfoReplicationIntervalSeconds = 40;

	private int eurekaServiceUrlPollIntervalSeconds = 5 * MINUTES;

	private String proxyPort;

	private String proxyHost;

	private String proxyUserName;

	private String proxyPassword;

	private int eurekaServerReadTimeoutSeconds = 8;

	private int eurekaServerConnectTimeoutSeconds = 5;

	private String backupRegistryImpl;

	private int eurekaServerTotalConnections = 200;

	private int eurekaServerTotalConnectionsPerHost = 50;

	private String eurekaServerURLContext;

	private String eurekaServerPort;

	private String eurekaServerDNSName;

	private String region = "us-east-1";

	private int eurekaConnectionIdleTimeoutSeconds = 30;

	private String registryRefreshSingleVipAddress;

	private int heartbeatExecutorThreadPoolSize = 2;

	private int heartbeatExecutorExponentialBackOffBound = 10;

	private int cacheRefreshExecutorThreadPoolSize = 2;

	private int cacheRefreshExecutorExponentialBackOffBound = 10;

	private Map<String, String> serviceUrl = new HashMap<>();

	private boolean useDnsForFetchingServiceUrls = false;

	private boolean registerWithEureka = true;

	private boolean preferSameZoneEureka = true;

	private boolean logDeltaDiff;

	private boolean disableDelta;

	private String fetchRemoteRegionsRegistry;

	private Map<String, String> availabilityZones = new HashMap<>();

	private boolean filterOnlyUpInstances = true;

	private boolean fetchRegistry = true;

	private String dollarReplacement = "_-";

	private String escapeCharReplacement = "__";

	private boolean allowRedirects = false;

	private boolean onDemandUpdateStatusChange = true;

	private String encoderName;

	private String decoderName;

	private String clientDataAccept = EurekaAccept.full.name();

	private boolean shouldUnregisterOnShutdown = true;

	private boolean shouldEnforceRegistrationAtInit = false;


	@Override
	public boolean shouldGZipContent() {
		return true;
	}

	@Override
	public boolean shouldUseDnsForFetchingServiceUrls() {
		return this.useDnsForFetchingServiceUrls;
	}

	@Override
	public boolean shouldRegisterWithEureka() {
		return this.registerWithEureka;
	}

	@Override
	public boolean shouldPreferSameZoneEureka() {
		return this.preferSameZoneEureka;
	}

	@Override
	public boolean shouldLogDeltaDiff() {
		return this.logDeltaDiff;
	}

	@Override
	public boolean shouldDisableDelta() {
		return this.disableDelta;
	}

	@Override
	public boolean shouldUnregisterOnShutdown() {
		return this.shouldUnregisterOnShutdown;
	}

	@Override
	public boolean shouldEnforceRegistrationAtInit() {
		return this.shouldEnforceRegistrationAtInit;
	}

	@Override
	public String fetchRegistryForRemoteRegions() {
		return this.fetchRemoteRegionsRegistry;
	}

	@Override
	public String[] getAvailabilityZones(String region) {
		String value = this.availabilityZones.get(region);
		if (value == null) {
			value = DEFAULT_ZONE;
		}
		return value.split(",");
	}

	@Override
	public List<String> getEurekaServerServiceUrls(String myZone) {
		String serviceUrls = this.serviceUrl.get(myZone);
		if (serviceUrls == null || serviceUrls.isEmpty()) {
			serviceUrls = this.serviceUrl.get(DEFAULT_ZONE);
		}
		if (!StringUtils.isEmpty(serviceUrls)) {
			final String[] serviceUrlsSplit = StringUtils.split(serviceUrls, ',');
					
			List<String> eurekaServiceUrls = new ArrayList<>(serviceUrlsSplit.length);
			for (String eurekaServiceUrl : serviceUrlsSplit) {
				if (!endsWithSlash(eurekaServiceUrl)) {
					eurekaServiceUrl += "/";
				}
				eurekaServiceUrls.add("http://"+eurekaServiceUrl.trim());
			}
			return eurekaServiceUrls;
		}

		return new ArrayList<>();
	}

	private boolean endsWithSlash(String url) {
		return url.endsWith("/");
	}

	@Override
	public boolean shouldFilterOnlyUpInstances() {
		return this.filterOnlyUpInstances;
	}

	@Override
	public boolean shouldFetchRegistry() {
		return this.fetchRegistry;
	}

	@Override
	public boolean allowRedirects() {
		return this.allowRedirects;
	}

	@Override
	public boolean shouldOnDemandUpdateStatusChange() {
		return this.onDemandUpdateStatusChange;
	}


	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int getRegistryFetchIntervalSeconds() {
		return registryFetchIntervalSeconds;
	}

	public void setRegistryFetchIntervalSeconds(int registryFetchIntervalSeconds) {
		this.registryFetchIntervalSeconds = registryFetchIntervalSeconds;
	}

	@Override
	public int getInstanceInfoReplicationIntervalSeconds() {
		return instanceInfoReplicationIntervalSeconds;
	}

	public void setInstanceInfoReplicationIntervalSeconds(
			int instanceInfoReplicationIntervalSeconds) {
		this.instanceInfoReplicationIntervalSeconds = instanceInfoReplicationIntervalSeconds;
	}

	@Override
	public int getInitialInstanceInfoReplicationIntervalSeconds() {
		return initialInstanceInfoReplicationIntervalSeconds;
	}

	public void setInitialInstanceInfoReplicationIntervalSeconds(
			int initialInstanceInfoReplicationIntervalSeconds) {
		this.initialInstanceInfoReplicationIntervalSeconds = initialInstanceInfoReplicationIntervalSeconds;
	}

	@Override
	public int getEurekaServiceUrlPollIntervalSeconds() {
		return eurekaServiceUrlPollIntervalSeconds;
	}

	public void setEurekaServiceUrlPollIntervalSeconds(
			int eurekaServiceUrlPollIntervalSeconds) {
		this.eurekaServiceUrlPollIntervalSeconds = eurekaServiceUrlPollIntervalSeconds;
	}

	@Override
	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	@Override
	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	@Override
	public String getProxyUserName() {
		return proxyUserName;
	}

	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}

	@Override
	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	@Override
	public int getEurekaServerReadTimeoutSeconds() {
		return eurekaServerReadTimeoutSeconds;
	}

	public void setEurekaServerReadTimeoutSeconds(int eurekaServerReadTimeoutSeconds) {
		this.eurekaServerReadTimeoutSeconds = eurekaServerReadTimeoutSeconds;
	}

	@Override
	public int getEurekaServerConnectTimeoutSeconds() {
		return eurekaServerConnectTimeoutSeconds;
	}

	public void setEurekaServerConnectTimeoutSeconds(
			int eurekaServerConnectTimeoutSeconds) {
		this.eurekaServerConnectTimeoutSeconds = eurekaServerConnectTimeoutSeconds;
	}

	@Override
	public String getBackupRegistryImpl() {
		return backupRegistryImpl;
	}

	public void setBackupRegistryImpl(String backupRegistryImpl) {
		this.backupRegistryImpl = backupRegistryImpl;
	}

	@Override
	public int getEurekaServerTotalConnections() {
		return eurekaServerTotalConnections;
	}

	public void setEurekaServerTotalConnections(int eurekaServerTotalConnections) {
		this.eurekaServerTotalConnections = eurekaServerTotalConnections;
	}

	@Override
	public int getEurekaServerTotalConnectionsPerHost() {
		return eurekaServerTotalConnectionsPerHost;
	}

	public void setEurekaServerTotalConnectionsPerHost(
			int eurekaServerTotalConnectionsPerHost) {
		this.eurekaServerTotalConnectionsPerHost = eurekaServerTotalConnectionsPerHost;
	}

	@Override
	public String getEurekaServerURLContext() {
		return eurekaServerURLContext;
	}

	public void setEurekaServerURLContext(String eurekaServerURLContext) {
		this.eurekaServerURLContext = eurekaServerURLContext;
	}

	@Override
	public String getEurekaServerPort() {
		return eurekaServerPort;
	}

	public void setEurekaServerPort(String eurekaServerPort) {
		this.eurekaServerPort = eurekaServerPort;
	}

	@Override
	public String getEurekaServerDNSName() {
		return eurekaServerDNSName;
	}

	public void setEurekaServerDNSName(String eurekaServerDNSName) {
		this.eurekaServerDNSName = eurekaServerDNSName;
	}

	@Override
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Override
	public int getEurekaConnectionIdleTimeoutSeconds() {
		return eurekaConnectionIdleTimeoutSeconds;
	}

	public void setEurekaConnectionIdleTimeoutSeconds(
			int eurekaConnectionIdleTimeoutSeconds) {
		this.eurekaConnectionIdleTimeoutSeconds = eurekaConnectionIdleTimeoutSeconds;
	}

	@Override
	public String getRegistryRefreshSingleVipAddress() {
		return registryRefreshSingleVipAddress;
	}

	public void setRegistryRefreshSingleVipAddress(
			String registryRefreshSingleVipAddress) {
		this.registryRefreshSingleVipAddress = registryRefreshSingleVipAddress;
	}

	@Override
	public int getHeartbeatExecutorThreadPoolSize() {
		return heartbeatExecutorThreadPoolSize;
	}

	public void setHeartbeatExecutorThreadPoolSize(int heartbeatExecutorThreadPoolSize) {
		this.heartbeatExecutorThreadPoolSize = heartbeatExecutorThreadPoolSize;
	}

	@Override
	public int getHeartbeatExecutorExponentialBackOffBound() {
		return heartbeatExecutorExponentialBackOffBound;
	}

	public void setHeartbeatExecutorExponentialBackOffBound(
			int heartbeatExecutorExponentialBackOffBound) {
		this.heartbeatExecutorExponentialBackOffBound = heartbeatExecutorExponentialBackOffBound;
	}

	@Override
	public int getCacheRefreshExecutorThreadPoolSize() {
		return cacheRefreshExecutorThreadPoolSize;
	}

	public void setCacheRefreshExecutorThreadPoolSize(
			int cacheRefreshExecutorThreadPoolSize) {
		this.cacheRefreshExecutorThreadPoolSize = cacheRefreshExecutorThreadPoolSize;
	}

	@Override
	public int getCacheRefreshExecutorExponentialBackOffBound() {
		return cacheRefreshExecutorExponentialBackOffBound;
	}

	public void setCacheRefreshExecutorExponentialBackOffBound(
			int cacheRefreshExecutorExponentialBackOffBound) {
		this.cacheRefreshExecutorExponentialBackOffBound = cacheRefreshExecutorExponentialBackOffBound;
	}

	public Map<String, String> getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(Map<String, String> serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public boolean isUseDnsForFetchingServiceUrls() {
		return useDnsForFetchingServiceUrls;
	}

	public void setUseDnsForFetchingServiceUrls(boolean useDnsForFetchingServiceUrls) {
		this.useDnsForFetchingServiceUrls = useDnsForFetchingServiceUrls;
	}

	public boolean isRegisterWithEureka() {
		return registerWithEureka;
	}

	public void setRegisterWithEureka(boolean registerWithEureka) {
		this.registerWithEureka = registerWithEureka;
	}

	public boolean isPreferSameZoneEureka() {
		return preferSameZoneEureka;
	}

	public void setPreferSameZoneEureka(boolean preferSameZoneEureka) {
		this.preferSameZoneEureka = preferSameZoneEureka;
	}

	public boolean isLogDeltaDiff() {
		return logDeltaDiff;
	}

	public void setLogDeltaDiff(boolean logDeltaDiff) {
		this.logDeltaDiff = logDeltaDiff;
	}

	public boolean isDisableDelta() {
		return disableDelta;
	}

	public void setDisableDelta(boolean disableDelta) {
		this.disableDelta = disableDelta;
	}

	public String getFetchRemoteRegionsRegistry() {
		return fetchRemoteRegionsRegistry;
	}

	public void setFetchRemoteRegionsRegistry(String fetchRemoteRegionsRegistry) {
		this.fetchRemoteRegionsRegistry = fetchRemoteRegionsRegistry;
	}

	public Map<String, String> getAvailabilityZones() {
		return availabilityZones;
	}

	public void setAvailabilityZones(Map<String, String> availabilityZones) {
		this.availabilityZones = availabilityZones;
	}

	public boolean isFilterOnlyUpInstances() {
		return filterOnlyUpInstances;
	}

	public void setFilterOnlyUpInstances(boolean filterOnlyUpInstances) {
		this.filterOnlyUpInstances = filterOnlyUpInstances;
	}

	public boolean isFetchRegistry() {
		return fetchRegistry;
	}

	public void setFetchRegistry(boolean fetchRegistry) {
		this.fetchRegistry = fetchRegistry;
	}

	@Override
	public String getDollarReplacement() {
		return dollarReplacement;
	}

	public void setDollarReplacement(String dollarReplacement) {
		this.dollarReplacement = dollarReplacement;
	}

	@Override
	public String getEscapeCharReplacement() {
		return escapeCharReplacement;
	}

	public void setEscapeCharReplacement(String escapeCharReplacement) {
		this.escapeCharReplacement = escapeCharReplacement;
	}

	public boolean isAllowRedirects() {
		return allowRedirects;
	}

	public void setAllowRedirects(boolean allowRedirects) {
		this.allowRedirects = allowRedirects;
	}

	public boolean isOnDemandUpdateStatusChange() {
		return onDemandUpdateStatusChange;
	}

	public void setOnDemandUpdateStatusChange(boolean onDemandUpdateStatusChange) {
		this.onDemandUpdateStatusChange = onDemandUpdateStatusChange;
	}

	@Override
	public String getEncoderName() {
		return encoderName;
	}

	public void setEncoderName(String encoderName) {
		this.encoderName = encoderName;
	}

	@Override
	public String getDecoderName() {
		return decoderName;
	}

	public void setDecoderName(String decoderName) {
		this.decoderName = decoderName;
	}

	@Override
	public String getClientDataAccept() {
		return clientDataAccept;
	}

	public void setClientDataAccept(String clientDataAccept) {
		this.clientDataAccept = clientDataAccept;
	}

	public boolean isShouldUnregisterOnShutdown() {
		return shouldUnregisterOnShutdown;
	}

	public void setShouldUnregisterOnShutdown(boolean shouldUnregisterOnShutdown) {
		this.shouldUnregisterOnShutdown = shouldUnregisterOnShutdown;
	}

	public boolean isShouldEnforceRegistrationAtInit() {
		return shouldEnforceRegistrationAtInit;
	}

	public void setShouldEnforceRegistrationAtInit(
			boolean shouldEnforceRegistrationAtInit) {
		this.shouldEnforceRegistrationAtInit = shouldEnforceRegistrationAtInit;
	}

	@Override
	public String getExperimental(String name) {
		return null;
	}

	@Override
	public EurekaTransportConfig getTransportConfig() {
		return null;
	}
}
