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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSON;

/**
 * 注册模型抽象类
 * @author jellly
 */
public abstract class AbstractRegisterModel implements RegisterModel<Object> {

	public static char URL_DIVISION = '/';

	private List<Field> nodeField = new ArrayList<>();
	
	protected RegisterServer<Object> registerServers;

	protected RegisterData registerData;

	public AbstractRegisterModel(RegisterServer<Object> registerServers, RegisterData registerData) {
		this.registerData = registerData;
		Class<?> dataClazz = registerData.getDataClass();
		if (Objects.nonNull(registerServers)) {
			this.registerServers = registerServers;
			monitor();
		} else {
			for (String nodeNode : registerData.getNodeName()) {
				nodeField.add(FieldUtils.getField(dataClazz, nodeNode, true));
			}
			register();
		}
	}

	/**
	 * 监控检查
	 */
	protected void monitor() {

	}

	/**
	 * 注册服务
	 */
	protected void register() {

	}

	/**
	 * 注销、下线服务
	 * @throws Exception
	 */
	public void deregister() throws Exception {
		
	}
	
	protected String getKey(Object object)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StringBuffer sb = new StringBuffer(registerData.getPath()).append('/');
		for (int i = 0; i < nodeField.size(); i++) {
			Object objectValue = nodeField.get(i).get(object);
			if (Objects.isNull(objectValue)) {
				throw new RuntimeException(nodeField.get(i).toString() + " is null , data is " + object.toString());
			}
			String data = objectValue.toString();
			sb.append(StringUtils.replaceChars(data, "/", "."));
			if (i < nodeField.size() - 1) {
				sb.append('~');
			}
		}
		return StringUtils.replace(sb.toString(), "/.", "/");
	}

	protected Object getRegisterDataObject(String value) {
		return JSON.parseObject(value, registerData.getDataClass());
	}
}