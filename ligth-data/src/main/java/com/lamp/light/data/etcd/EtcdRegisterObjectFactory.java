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
package com.lamp.light.data.etcd;

import com.lamp.electron.register.api.RegisterData;
import com.lamp.electron.register.api.RegisterModel;
import com.lamp.electron.register.api.RegisterObjectFactory;
import com.lamp.electron.register.api.RegisterServer;

/**
 * Etcd注册工厂
 * @author jellly
 */
public class EtcdRegisterObjectFactory implements RegisterObjectFactory {

	private EtcdClientFactory etcdClientFactory = new EtcdClientFactory();
	
	@Override
	public String registerCenterName() {
		return "etcd";
	}
	
	@Override
	public  boolean electronRegister() {
		return true;
	}

	@Override
	public RegisterModel<Object> createRegisterModel(RegisterServer<Object> registerServers,
			RegisterData registerData) {
		return new EtcdRegister(etcdClientFactory, registerServers, registerData);
	}
	
	

}
