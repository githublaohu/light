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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response.Status;

import com.alibaba.fastjson.JSONObject;
import com.lamp.electron.register.api.AbstractRegisterModel;
import com.lamp.electron.register.api.RegisterData;
import com.lamp.electron.register.api.RegisterServer;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.ActionType;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.resolver.ClosableResolver;
import com.netflix.discovery.shared.resolver.EndpointRandomizer;
import com.netflix.discovery.shared.resolver.ResolverUtils;
import com.netflix.discovery.shared.resolver.aws.ApplicationsResolver;
import com.netflix.discovery.shared.resolver.aws.AwsEndpoint;
import com.netflix.discovery.shared.transport.DefaultEurekaTransportConfig;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpClientFactory;
import com.netflix.discovery.shared.transport.EurekaHttpClients;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.TransportClientFactory;
import com.netflix.discovery.shared.transport.jersey.Jersey1TransportClientFactories;
import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;
import com.sun.jersey.api.client.filter.ClientFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * Eureka注册模型
 * @author jellly
 */
@Slf4j
public class EurekaRegister extends AbstractRegisterModel {

	private EurekaHttpClient queryClient;

	private EurekaHttpClientFactory eurekaHttpClientFactory;

	private TransportClientFactory transportClientFactory;

	private Timer timer = new Timer();

	private volatile boolean isGetApplications = false;

	public EurekaRegister(RegisterServer<Object> registerServers, RegisterData registerData) {
		super(registerServers, registerData);
		init();
	}

	public void init() {
		InstanceInfo.Builder builder = InstanceInfo.Builder.newBuilder();
		builder.setAppName("electron-register");
		InstanceInfo instanceInfo = builder.build();

		ElectronEurekaClientConfig clientConfig = new ElectronEurekaClientConfig();
		Map<String, String> serviceUrl = new HashMap<>();
		serviceUrl.put("defaultZone",registerData.getServerUrl());
		clientConfig.setServiceUrl(serviceUrl);
		
		DefaultEurekaTransportConfig transportConfig = new DefaultEurekaTransportConfig("",
				DynamicPropertyFactory.getInstance());

		TransportClientFactories<ClientFilter> transportClientFactories = new Jersey1TransportClientFactories();
		transportClientFactory = transportClientFactories.newTransportClientFactory(clientConfig,
				Collections.emptyList(), instanceInfo, Optional.empty(), Optional.empty());

		ApplicationsResolver.ApplicationsSource applicationsSource = new ApplicationsResolver.ApplicationsSource() {
			@Override
			public Applications getApplications(int stalenessThreshold, TimeUnit timeUnit) {
				return null;
			}
		};

		EndpointRandomizer endpointRandomizer = ResolverUtils::randomize;
		ClosableResolver<AwsEndpoint> bootstrapResolver = EurekaHttpClients.newBootstrapResolver(clientConfig, transportConfig,
				transportClientFactory, instanceInfo, applicationsSource, endpointRandomizer);

		eurekaHttpClientFactory = EurekaHttpClients.queryClientFactory(bootstrapResolver, transportClientFactory,
				clientConfig, transportConfig, instanceInfo, applicationsSource, endpointRandomizer);
		queryClient = eurekaHttpClientFactory.newClient();
	}

	@Override
	protected void monitor() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				EurekaRegister.this.getApplications();
			}
		}).start();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				getApplications();
			}
		}, 10000, 10000);

	}

	private void getApplications() {
		if (!isGetApplications) {
			EurekaHttpResponse<Applications> response = queryClient.getApplications(new String[0]);

			if (response.getStatusCode() != Status.OK.getStatusCode()) {
				log.info("eureka register pull applications fail, status code is {}", response.getStatusCode());
			} else {
				log.info("eureka register pull applications success");
				isGetApplications = true;
				Applications applications = response.getEntity();
				applications.getRegisteredApplications().forEach(e -> {
					e.getInstances().forEach(ie -> {
						registerServers.register(toInstanceInfo(ie));
					});
				});
				log.info("getApplications pull applications info is {}", applications);
			}
		} else {
			EurekaHttpResponse<Applications> response = queryClient.getDelta(new String[0]);
			if (response.getStatusCode() != Status.OK.getStatusCode()) {
				log.info("eureka register pull getDelta fail, status code is {}", response.getStatusCode());
			}
			Applications applications = response.getEntity();
			applications.getRegisteredApplications().forEach(e -> {
				e.getInstances().forEach(ie -> {
					if (ActionType.ADDED.equals(ie.getActionType()) || ActionType.MODIFIED.equals(ie.getActionType())) {
						registerServers.register(toInstanceInfo(ie));
					} else {
						registerServers.deregister(toInstanceInfo(ie));
					}
				});
			});
			log.info("getDelta pull applications info is {}", applications);
		}
	}

	private Object toInstanceInfo(InstanceInfo instanceInfo) {
		JSONObject mapper = new JSONObject();
		mapper.put("networkAddress", instanceInfo.getIPAddr());
		mapper.put("port", instanceInfo.getPort());
		mapper.put("applicationEnglishName", instanceInfo.getAppName());
		mapper.put("protocol", "HTTP");
		return mapper.toJavaObject(registerData.getDataClass());
	}

	@Override
	public int register(Object t) {
		return 0;
	}

	@Override
	public int deregister(Object t) {
		return 0;
	}
}
