package com.lamp.light;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.lamp.light.handler.HandleProxy;
import com.lamp.light.serialize.FastJsonSerialize;
import com.lamp.light.serialize.Serialize;

public class Light {

    private InetSocketAddress inetSocketAddress;

    private List<Interceptor> interceptorList;

    private Serialize serialize;

    private String path;

    public <T> T create(Class<?> clazz) throws Exception {
        validateServiceInterface(clazz);
        return create(clazz, null);
    }

    public <T> T create(Class<?> clazz, Object result) throws Exception {
        return create(clazz, result, result);
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> clazz, Object success, Object fail) throws Exception {
        validateServiceInterface(clazz);
        return (T)getObject(clazz, success , fail);
    }

    private void validateServiceInterface(Class<?> clazz) {
        if (Objects.isNull(clazz)) {

        }
        if (!clazz.isInterface()) {

        }
    }

    private Object getObject(Class<?> clazz, Object success, Object fail) throws Exception {
        HandleProxy handleProxy =
            new HandleProxy(path, clazz, inetSocketAddress, interceptorList, serialize, success, fail);
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, handleProxy);
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        private String scheme = "http1.1";

        private String host;

        int port = 80;

        String path;

        Serialize serialize;

        List<Interceptor> interceptorList;

        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder serialize(Serialize serialize) {
            this.serialize = serialize;
            return this;
        }

        public Builder interceptor(Interceptor interceptor) {
            if (this.interceptorList == null) {
                interceptorList = new ArrayList<Interceptor>();
            }
            this.interceptorList.add(interceptor);
            return this;
        }

        public Light build() {
            Light light = new Light();

            if (Objects.isNull(host) && "".equals(host)) {

            }

            if (Objects.isNull(serialize)) {
                this.serialize = new FastJsonSerialize();
            }
            light.inetSocketAddress = new InetSocketAddress(host, port);
            light.serialize = serialize;
            light.interceptorList = interceptorList;
            return light;
        }
    }
}
