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
package com.lamp.light.data;

import java.util.List;

/**
 * 注册工厂
 * @author jellly
 */
public interface RegisterFactory {

	public <T> T createRegisterObject(Class<?> clazz) ;
	
	public void createMonitorObjectTo(List<RegisterServer<?>> registerServerList) throws Exception ;
	
	public void createMonitorObject(RegisterServer<?> registerServerList) throws Exception;
	
	public void createMonitorObject(List<RegisterServer<Object>> registerServerList, String url, String prefix)
			throws Exception;
}
