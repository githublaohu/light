/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PSL v2.
 *You can use this software according to the terms and conditions of the Mulan PSL v2.
 *You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PSL v2 for more details.
 */
package com.lamp.light.handler;

import com.lamp.light.Interceptor;
import com.lamp.light.handler.Coordinate.ParametersType;
import com.lamp.light.handler.CoordinateHandler.CoordinateHandlerWrapper;
import com.lamp.light.netty.NettyClient;
import com.lamp.light.response.ReturnMode;
import com.lamp.light.serialize.Serialize;
import com.lamp.light.util.BaseUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder.ErrorDataEncoderException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HandleProxy  处理请求的代理类 <BR>
 * <li>1. 解析所有的请求信息，与返回基本信息</li>
 * <li>2. 把解析出来的信息，生产netty Request对象</li>
 * <li>3. 创建有效的 http china </li>
 * <li>4. 发送</li>
 */
public class HandleProxy implements InvocationHandler {

    private AnnotationAnalysis annotationAnalysis = new AnnotationAnalysis();

    private Map<Method, HandleMethod> handleMethodMap = new ConcurrentHashMap<>();

    // http1.1支持
    private NettyClient nettyClient;

    private InetSocketAddress inetSocketAddress;
    
    private String socketAddress;

    private List<Interceptor> interceptorList = new ArrayList<>();

    private RequestInfo requestInfo;

    private Serialize serialize;

    private Object success;

    private Object fail;

    public HandleProxy(NettyClient nettyClient,String path, Class<?> proxy, InetSocketAddress inetSocketAddress, List<Interceptor> interceptorList,
                       Serialize serialize, Object success, Object fail) throws Exception {
    	this.nettyClient = nettyClient;
        if (Objects.isNull(success) || Objects.isNull(fail)) {

        }
        //  根据当前clazz解析请求数据
        this.requestInfo = annotationAnalysis.analysis(proxy);
        //  在 annotationAnalysis.analysis(proxy) 方法中已拿到了 url 这里进行二次计算
        this.requestInfo.setUrl(BaseUtils.setSlash(path) + BaseUtils.setSlash(requestInfo.getUrl()));
        this.interceptorList = interceptorList;
        this.serialize = serialize;
        this.inetSocketAddress = inetSocketAddress;
        this.success = success;
        this.fail = fail;
        this.socketAddress = inetSocketAddress.toString();
        this.socketAddress = socketAddress.substring(1,socketAddress.length());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        //  以下没有method.invoke
        HandleMethod handleMethod = handleMethodMap.get(method);
        if (Objects.isNull(handleMethod)) {
            handleMethod = new HandleMethod();
            handleMethod.requestInfo = annotationAnalysis.analysis(method, this.requestInfo);
            if (Objects.nonNull(success)) {
                handleMethod.success = success;
                handleMethod.fail = fail;
                handleMethod.returnMode = ReturnMode.ASYSN;
            } else {
                handleMethod.returnMode = handleMethod.requestInfo.getReturnMode();
            }
            handleMethodMap.put(method, handleMethod);
        }
        RequestInfo requestInfo = handleMethod.requestInfo;
        if (Objects.nonNull(interceptorList)) {
            for (Interceptor interceptor : interceptorList) {
                args = interceptor.handlerBefore(proxy, method, requestInfo, args);
            }
        }
        HttpRequest defaultFullHttpRequest = getHttpRequest(args, handleMethod);
        if (Objects.nonNull(interceptorList)) {
            for (Interceptor interceptor : interceptorList) {
            	defaultFullHttpRequest = interceptor.handlerRequest(requestInfo, defaultFullHttpRequest);
            }
        }
        AsynReturn asynReturn = new AsynReturn();
        asynReturn.setReturnMode(handleMethod.returnMode);
        asynReturn.setFullHttpRequest(defaultFullHttpRequest);
        asynReturn.setHandleMethod(handleMethod);
        asynReturn.setSerialize(serialize);
        nettyClient.write(asynReturn, inetSocketAddress);
        Object object = null;
        if (handleMethod.returnMode == ReturnMode.SYNS) {
            object = asynReturn.getObject();
        } else if (handleMethod.returnMode == ReturnMode.CALL) {
            asynReturn.setCall(new DefaultCall<>(asynReturn, nettyClient, inetSocketAddress));
            object = asynReturn.getCall();
        }
        return object;
    }

    public HttpRequest getHttpRequest(Object[] args, HandleMethod handleMethod) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ErrorDataEncoderException {
        CoordinateHandlerWrapper coordinateHandlerWrapper = CoordinateHandler.getCoordinateHandlerWrapper();
        RequestInfo requestInfo = handleMethod.requestInfo;
        // path
        if (Objects.nonNull(requestInfo.getPathList())) {
            coordinateHandlerWrapper.pathCoordinateHandler.setObject(requestInfo.getUrl());
            coordinateHandler(args, requestInfo.getPathList(), coordinateHandlerWrapper.pathCoordinateHandler);
        }
        // query
        QueryStringEncoder queryStringEncoder = new QueryStringEncoder(requestInfo.getUrl());
        if (Objects.nonNull(requestInfo.getQueryList())) {
            coordinateHandlerWrapper.queryCoordinateHandler.setObject(queryStringEncoder);
            coordinateHandler(args, requestInfo.getQueryList(), coordinateHandlerWrapper.queryCoordinateHandler);
        }
        // header
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        Set<Entry<String, String>> it = requestInfo.getHeader().entrySet();
        for (Entry<String, String> e : it) {
            httpHeaders.add(e.getKey(), e.getValue());
        }
        if (Objects.nonNull(requestInfo.getHeaderList())) {
            coordinateHandlerWrapper.headerCoordinateHandler.setObject(httpHeaders);
            coordinateHandler(args, requestInfo.getHeaderList(), coordinateHandlerWrapper.headerCoordinateHandler);
        }

        // HttpPostRequestEncoder 用于post请求
        HttpRequest defaultFullHttpRequest;
        ByteBuf buffer = Unpooled.EMPTY_BUFFER;
        if (Objects.equals(HttpMethod.POST, handleMethod.requestInfo.getHttpMethod())) {
            if (requestInfo.getIsBody()) {
                // body 协议 httpHeaders
                byte[] bytes = serialize.serialize(args[requestInfo.getBodyIndex()]);
                buffer = Unpooled.directBuffer(bytes.length).writeBytes(bytes);
                httpHeaders.set("Content-Type", "application/json");
                httpHeaders.set("Content-Length", bytes.length);
            }
        }
        httpHeaders.set(HttpHeaderNames.HOST,socketAddress);
        ClientCookieEncoder clientCookieEncoder = ClientCookieEncoder.STRICT;
        defaultFullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, requestInfo.getHttpMethod(),
                queryStringEncoder.toString(), buffer, httpHeaders, httpHeaders);
        if (Objects.nonNull(requestInfo.getFieldList())) {
            HttpPostRequestEncoder httpPostRequestEncoder = new HttpPostRequestEncoder(defaultFullHttpRequest, false);
            coordinateHandlerWrapper.fieldCoordinateHandler.setObject(httpPostRequestEncoder);
            coordinateHandler(args, requestInfo.getFieldList(), coordinateHandlerWrapper.fieldCoordinateHandler);
            //defaultFullHttpRequest = httpPostRequestEncoder.finalizeRequest();
        }
        return defaultFullHttpRequest;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void coordinateHandler(Object[] args, List<Coordinate> coordinateList, CoordinateHandler coordinateHandler)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 要支持form的list
        for (Coordinate coordinate : coordinateList) {
            Object object = args[coordinate.getIndex()];
            if (Objects.equals(coordinate.getType(), ParametersType.BASIC)
                    || Objects.equals(coordinate.getType(), ParametersType.PACKING)) {
                coordinateHandler.handler(coordinate.getKey(), object.toString());
            } else if (Objects.equals(coordinate.getType(), ParametersType.STRING)) {
                coordinateHandler.handler(coordinate.getKey(), (String) object);
            } else if (Objects.equals(coordinate.getType(), ParametersType.MAP)) {
                Map<String, Object> map = (Map<String, Object>) object;
                if (Objects.isNull(coordinate.getKey())) {
                    for (Entry<String, Object> e : map.entrySet()) {
                        coordinateHandler.handler(e.getKey(), TypeToString.ObjectToString(e.getValue()));
                    }
                } else {
                    coordinateHandler.handler(coordinate.getKey(),
                            TypeToString.ObjectToString(map.get(coordinate.getKey())));
                }
            } else if (Objects.equals(coordinate.getType(), ParametersType.OBJECT)) {
                coordinateHandler.handler(coordinate.getKey(),
                        TypeToString.ObjectToString(coordinate.getMethod().invoke(object)));
            }
        }
        coordinateHandler.clean();
    }


    public static class HandleMethod {
        private RequestInfo requestInfo;

        private Method method;

        private Serialize serialize;

        private Integer requestTimes = 30000;

        private Object success;

        private Object fail;

        private ReturnMode returnMode;

        public RequestInfo getRequestInfo() {
            return requestInfo;
        }

        public void setRequestInfo(RequestInfo requestInfo) {
            this.requestInfo = requestInfo;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Serialize getSerialize() {
            return serialize;
        }

        public void setSerialize(Serialize serialize) {
            this.serialize = serialize;
        }

        public Integer getRequestTimes() {
            return requestTimes;
        }

        public void setRequestTimes(Integer requestTimes) {
            this.requestTimes = requestTimes;
        }

        public Object getSuccess() {
            return success;
        }

        public void setSuccess(Object success) {
            this.success = success;
        }

        public Object getFail() {
            return fail;
        }

        public void setFail(Object fail) {
            this.fail = fail;
        }

        public ReturnMode getReturnMode() {
            return returnMode;
        }

        public void setReturnMode(ReturnMode returnMode) {
            this.returnMode = returnMode;
        }


    }
}
