package com.lamp.light;

import java.lang.reflect.Method;

import com.lamp.light.handler.RequestInfo;

public interface Interceptor {

    public Object[] handlerBefore(Object proxy, Method method , RequestInfo requestInfo , Object[] args);
}
