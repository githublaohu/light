package com.lamp.ligth.model;

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
        if( !LigthBaseReturnObject.class.isAssignableFrom(clazz) ) {
            return null;
        }
        Constructor<?> constructor = constructorMap.get(clazz);
        if (Objects.isNull(constructor)) {

            constructor = clazz.getConstructor(new Class[0]);
            constructorMap.put(clazz, constructor);
        }
        Object object = constructor.newInstance();
        if(object instanceof LigthBaseReturnObject) {
            LigthBaseReturnObject ligthBaseReturnObject = (LigthBaseReturnObject)object;
            ligthBaseReturnObject.setSuccess(false);
            ligthBaseReturnObject.setThrowable(throwable);
        }
        return object;
    }

}
