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
package com.lamp.light.api.data;

import com.lamp.light.api.data.monitor.MonitorServices;
import com.lamp.light.api.data.register.RegisterData;
import com.lamp.light.api.data.register.RegisterServer;
import com.lamp.light.api.data.register.RegisterServices;

public interface RegisterObjectFactory {

	public String registerCenterName();
	
	public default boolean electronRegister() {
		return false;
	}
	
	public RegisterServices createRegisterServices(RegisterServer<Object> registerServers, RegisterData registerData);


	public MonitorServices createMonitorServices(RegisterServer<Object> registerServers, RegisterData registerData);
}
