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
package com.lamp.light.data.nacos;


import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.lamp.electron.register.api.AbstractRegisterModel;
import com.lamp.electron.register.api.RegisterData;
import com.lamp.electron.register.api.RegisterServer;

/**
 * nacos注册模型
 * TODO
 * @author jellly
 */
public class NacosRegister extends AbstractRegisterModel {

	private static final Logger log = LoggerFactory.getLogger(NacosRegister.class);

	private NamingService namingService = null;

	public NacosRegister(RegisterServer<Object> registerServers, RegisterData registerData) {
		super(registerServers, registerData);
		String servcerUrl = registerData.getServerUrl();
		try {
			URL url = new URL("http://"+servcerUrl);
			Properties properties = new Properties();
			properties.put("serverAddr", url.getHost() + ":" + url.getPath());
			for (String data : StringUtils.split(url.getQuery(), "&")) {
				String[] dataArray = StringUtils.split(data, "=");
				properties.put(dataArray[0], dataArray[1]);
			}
			namingService = NamingFactory.createNamingService(properties);
			new Thread(new NacosRuntime()).start();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	class NacosRuntime implements Runnable {

		private Set<String> currentService = new HashSet<>();

		private Set<String> newestService = new HashSet<>();

		private Map<String, List<Instance>> currentInstances = new HashMap<>();

		@Override
		public void run() {
			while (true) {
				try {
					while (true) {
						Thread.sleep(5000);
						this.getServices();
						if (Objects.isNull(newestService) || newestService.isEmpty()) {
							continue;
						}
						// 已经删除的服务
						Set<String> newCurrentService = new HashSet<>(currentService);
						newCurrentService.removeAll(newestService);
						for (String service : newCurrentService) {

						}
						newestService = currentService;
						for (String serviceName : newestService) {
							List<Instance> newestInstance = namingService.selectInstances(serviceName, true);
							List<Instance> currentInstance = currentInstances.get(serviceName);
							if (Objects.isNull(currentInstance)) {
								for (Instance instance : newestInstance) {
									this.register(instance);
								}
							} else {
								this.classification(currentInstance, newestInstance);
							}
							currentInstances.put(serviceName, newestInstance);
						}

					}
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
				}
			}

		}

		private Object createExampleInfo(Instance instance) {
			JSONObject exampleInfo = new JSONObject();
			exampleInfo.put("applicationEnglishName", instance.getServiceName());
			exampleInfo.put("name", instance.getServiceName());
			exampleInfo.put("networkAddress", instance.getIp());
			exampleInfo.put("port", instance.getPort());
			exampleInfo.put("protocol", "HTTP");
			return exampleInfo.toJavaObject(NacosRegister.this.registerData.getDataClass());
		}

		private void register(Instance instance) {
			NacosRegister.this.registerServers.register(this.createExampleInfo(instance));
		}

		private void classification(List<Instance> currentInstance, List<Instance> newestInstance) {
			Map<String, Instance> oldHostMap = new HashMap<String, Instance>(currentInstance.size());
			for (Instance host : currentInstance) {
				oldHostMap.put(host.toInetAddr(), host);
			}
			Map<String, Instance> newHostMap = new HashMap<String, Instance>(newestInstance.size());
			for (Instance host : newestInstance) {
				newHostMap.put(host.toInetAddr(), host);
			}
			List<Map.Entry<String, Instance>> newServiceHosts = new ArrayList<Map.Entry<String, Instance>>(
					newHostMap.entrySet());
			for (Map.Entry<String, Instance> entry : newServiceHosts) {
				Instance host = entry.getValue();
				String key = entry.getKey();
				if (oldHostMap.containsKey(key)
						&& !StringUtils.equals(host.toString(), oldHostMap.get(key).toString())) {
					this.register(host);
					continue;
				}

				if (!oldHostMap.containsKey(key)) {
					this.register(host);
				}
			}
			for (Map.Entry<String, Instance> entry : oldHostMap.entrySet()) {
				Instance host = entry.getValue();
				String key = entry.getKey();
				if (newHostMap.containsKey(key)) {
					continue;
				}
				if (!newHostMap.containsKey(key)) {
					NacosRegister.this.registerServers.deregister(this.createExampleInfo(host));
				}
			}
		}

		void getServices() throws NacosException {
			int pageNo = 0;
			while (true) {
				ListView<String> listView = namingService.getServicesOfServer(pageNo++, 100);
				newestService.addAll(listView.getData());
				if (listView.getCount() < 100) {
					return;
				}
			}
		}
	}

	@Override
	public int register(Object t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deregister(Object t) {
		// TODO Auto-generated method stub
		return 0;
	}
}
