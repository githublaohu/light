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
package com.lamp.light.api.data.register;

import java.util.Arrays;

public class RegisterData {
	
	
	public static RegisterData cloneObject(RegisterData registerData) {
		RegisterData newRegisterData = new RegisterData();
		newRegisterData.setNodeName(registerData.getNodeName());
		newRegisterData.setPersistence(registerData.isPersistence());
		newRegisterData.setPath(registerData.getPath());
		newRegisterData.setDataClass(registerData.getDataClass());
		newRegisterData.setServerUrl(registerData.getServerUrl());
		return newRegisterData;
	}
	

	private String path;

	private String[] nodeName;
	
	private boolean persistence;

	private Class<?> dataClass;

	private String serverUrl;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String[] getNodeName() {
		return nodeName;
	}

	public void setNodeName(String[] nodeName) {
		this.nodeName = nodeName;
	}

	public boolean isPersistence() {
		return persistence;
	}

	public void setPersistence(boolean persistence) {
		this.persistence = persistence;
	}

	public Class<?> getDataClass() {
		return dataClass;
	}

	public void setDataClass(Class<?> dataClass) {
		this.dataClass = dataClass;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	@Override
	public String toString() {
		return "RegisterData [path=" + path + ", nodeName=" + Arrays.toString(nodeName) + ", persistence=" + persistence
				+ ", dataClass=" + dataClass + ", serverUrl=" + serverUrl + "]";
	}
}
