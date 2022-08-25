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
package com.lamp.light.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.lamp.light.netty.NettyClient;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ModelManage {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(NettyClient.class);

	private static final ModelManage MANAGE = new ModelManage();

	public static final ModelManage getInstance() {
		return MANAGE;
	}

	private ConcurrentHashMap<Type, Constructor<?>> constructorMap = new ConcurrentHashMap<>();
	
	private  ConcurrentHashMap<Type, Boolean> errerConstructorMap = new ConcurrentHashMap<>();

	public ModelManage() {
	};

	public Object getModel(Type type, Throwable throwable, DefaultHttpResponse defaultHttpResponse, ByteBuf connect)  {
		try {
			Class<?> clazz = (Class<?>) type;
			if (errerConstructorMap.containsKey(type)) {
				return null;
			}
			if (!LightBaseReturnObject.class.isAssignableFrom(clazz)) {
				errerConstructorMap.put(type,true);
				return null;
			}
			Constructor<?> constructor = constructorMap.get(type);
			if (Objects.isNull(constructor)) {
				constructor = clazz.getConstructor(new Class[0]);
				constructorMap.put(type, constructor);
			}
			Object object = constructor.newInstance();
			if (object instanceof LightBaseReturnObject) {
				LightBaseReturnObject LightBaseReturnObject = (LightBaseReturnObject) object;
				LightBaseReturnObject.setSuccess(false);
				LightBaseReturnObject.setThrowable(throwable);
			}
			return object;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			errerConstructorMap.put(type, true);
			return null;
		}
	}

}
