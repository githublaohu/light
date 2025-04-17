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
package com.lamp.light.data.consul;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.catalog.CatalogConsulClient;
import com.ecwid.consul.v1.catalog.CatalogServicesRequest;
import com.ecwid.consul.v1.health.HealthConsulClient;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import com.lamp.electron.register.api.AbstractRegisterModel;
import com.lamp.electron.register.api.RegisterData;
import com.lamp.electron.register.api.RegisterServer;

public class ConsulRegister extends AbstractRegisterModel {
	

	private HealthConsulClient healthConsulClient;
	
	private CatalogConsulClient catalogClient;
	
	public ConsulRegister(RegisterServer<Object> registerServers, RegisterData registerData) {
		super(registerServers, registerData);
	}
	
	@Override
	protected void monitor() {
		ConsulRawClient consulRawClient = new ConsulRawClient(); 
		healthConsulClient = new HealthConsulClient(consulRawClient);
		catalogClient = new CatalogConsulClient(consulRawClient);
		new Thread(new ConsulNotifier()).start();
	}

	/**
	 * todo
	 * @param t
	 * @return
	 */
	@Override
	public int register(Object t) {
		return 0;
	}

	/**
	 * todo
	 * @param t
	 * @return
	 */
	@Override
	public int deregister(Object t) {
		return 0;
	}
	
	private class ConsulNotifier implements Runnable {

		
		private Long consulIndex = 0L;
		
		private Set<HealthService.Service> lastService;
		
		
        @Override
        public void run() {
        	while(true) {
	            //  轮训URL
	            try {
	            	while(true) {
	            		processServices("*");
	            		Thread.sleep(5000);
	            	}
	            } catch (Throwable e) {
	            	
	            }

        	}
        }


        private void processServices(String path) {
        	// 查询所有服务
            Response<Map<String, List<String>>> response = getAllServices(consulIndex, 3000);

            // 如果consul里面的注册信息发现了改变，那么会对currentIndex进行增加
            Long currentIndex = response.getConsulIndex();
            //
            if (Objects.isNull(currentIndex) || currentIndex < this.consulIndex) {
            	return;
            }
            this.consulIndex = currentIndex;
            // 查询所有健康的服务
            List<HealthService> services = getHealthServices(response.getValue());
            //  获得所有服务
            Set<HealthService.Service> currentService = services.stream().map(HealthService::getService).collect(Collectors.toSet());
            
            // 比较然后通知
            Set<HealthService.Service> difference = new HashSet<>(lastService);
            difference.removeAll(currentService);
            // 删除的
            
            // 添加的
            difference.clear();
            difference.addAll(currentService);
            difference.removeAll(lastService);
            lastService = currentService;
        }
    }
	
    private Response<List<HealthService>> getHealthServices(String service, long index, int watchTimeout) {
        HealthServicesRequest request = HealthServicesRequest.newBuilder()
                .setQueryParams(new QueryParams(watchTimeout, index))
                .setPassing(true)
                .build();
        return healthConsulClient.getHealthServices(service, request);
    }
	
    private Response<Map<String, List<String>>> getAllServices(long index, int watchTimeout) {
        CatalogServicesRequest request = CatalogServicesRequest.newBuilder()
                .setQueryParams(new QueryParams(watchTimeout, index))
                .build();
        return catalogClient.getCatalogServices(request);
    }

    private List<HealthService> getHealthServices(Map<String, List<String>> services) {
        return services.keySet().stream()
                .map(s -> getHealthServices(s, -1, -1).getValue())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
