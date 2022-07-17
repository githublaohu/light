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

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpResponse;

public class ModelManage {

    private static final ModelManage MANAGE = new ModelManage();
    
    public static final ModelManage getInstance() {
        return MANAGE;
    }
    
    private ConcurrentHashMap<Class<?>, Constructor<?>> constructorMap = new ConcurrentHashMap<>();

    
    public ModelManage() {};
    
    public Object getModel(Type type,Throwable throwable,DefaultHttpResponse defaultHttpResponse,ByteBuf connect) throws Exception {
        Class<?> clazz = (Class<?>)type;
        if( !LightBaseReturnObject.class.isAssignableFrom(clazz) ) {
            return null;
        }
        Constructor<?> constructor = constructorMap.get(clazz);
        if (Objects.isNull(constructor)) {

            constructor = clazz.getConstructor(new Class[0]);
            constructorMap.put(clazz, constructor);
        }
        Object object = constructor.newInstance();
        if(object instanceof LightBaseReturnObject) {
            LightBaseReturnObject LightBaseReturnObject = (LightBaseReturnObject)object;
            LightBaseReturnObject.setSuccess(false);
            LightBaseReturnObject.setThrowable(throwable);
        }
        return object;
    }

}
