package com.lamp.light.cloud;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import com.lamp.light.cloud.implement.CloudImplementObject;

/**
 * 什么模式我是知道的
 * 加入配置中心或者管理中心就有
 * 通过名字拉取配置
 *
 * @author laohu
 */
public class CircuitInvocationHandler implements InvocationHandler {

    private Class<?> proxy;

    private Map<String, Map<String, Object>> tager;

    private Object only;

    private CloudImplementObject cloudImplementObject;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }


        return null;
    }

}
