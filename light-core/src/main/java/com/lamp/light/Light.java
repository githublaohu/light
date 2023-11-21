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
package com.lamp.light;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLException;

import com.lamp.light.api.interceptor.Interceptor;
import com.lamp.light.api.route.RouteSelect;
import com.lamp.light.api.serialize.Serialize;
import com.lamp.light.handler.HandlerProxy;
import com.lamp.light.netty.NettyClient;
import com.lamp.light.route.DefaultRouteSelect;
import com.lamp.light.serialize.FastJsonSerialize;

/**
 * @author laohu
 */
public class Light {
    /**
     * ip address & port number
     */
    private InetSocketAddress inetSocketAddress;

    private RouteSelect routeSelect;

    private List<Interceptor> interceptorList;

    private Serialize serialize;

    private String path;

    private Executor executor;

    private NettyClient nettyClient;

    private boolean isTLS = false;

    private Map<String, String> contextData = new HashMap<>();


    private void init() {
        try {
            nettyClient = new NettyClient(executor);
        } catch (CertificateException | SSLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param clazz the class that needs to be proxied
     * @param <T>   type
     * @return class
     * @throws Exception 未知
     */
    public <T> T create(Class<?> clazz) throws Exception {
        // check 其中create
        validateServiceInterface(clazz);
        // create
        return create(clazz, null);
    }

    public <T> T create(Class<?> clazz, Object result) throws Exception {
        return create(clazz, result, result);
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> clazz, Object success, Object fail) throws Exception {
        return (T) getObject(clazz, success, fail);
    }

    /**
     * check
     *
     * @param clazz
     */
    private void validateServiceInterface(Class<?> clazz) {
        if (Objects.isNull(clazz)) {

        }
        if (!clazz.isInterface()) {

        }
    }

    /**
     * get proxy instance
     *
     * @param clazz
     * @param success
     * @param fail
     * @return object
     * @throws Exception 未知
     */
    private Object getObject(Class<?> clazz, Object success, Object fail) throws Exception {
        // 创建执行逻辑代理类
        HandlerProxy handleProxy =
                // 真实执行
                new HandlerProxy(nettyClient, path, clazz, routeSelect, interceptorList, serialize, success, fail);
        handleProxy.setTSL(this.isTLS);
        // 为当前 clazz 返回
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handleProxy);
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        @SuppressWarnings("unused")
        private String scheme = "http1.1";

        private String host;

        private boolean isTLS = false;

        private int port = 80;

        private String path;

        private Serialize serialize;

        private List<Interceptor> interceptorList;

        private RouteSelect routeSelect;

        private Executor executor;

        private Map<String, String> contextData;

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

        public Builder routeSelect(RouteSelect routeSelect) {
            this.routeSelect = routeSelect;
            return this;
        }

        public Builder tls(boolean tls) {
            this.isTLS = tls;
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
            if (Objects.isNull(routeSelect)) {
                light.inetSocketAddress = new InetSocketAddress(host, port);
                this.routeSelect = new DefaultRouteSelect(light.inetSocketAddress);
            }
            light.routeSelect = routeSelect;
            light.serialize = serialize;
            light.interceptorList = interceptorList;
            if (Objects.isNull(this.executor)) {
                executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                        Runtime.getRuntime().availableProcessors(), 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
                    AtomicInteger atomicInteger = new AtomicInteger();

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "ligth-asyn-" + atomicInteger.incrementAndGet());
                    }
                });
            }
            light.path = path;
            light.executor = executor;
            light.isTLS = isTLS;
            light.init();
            return light;
        }
    }
}
