package com.lamp.light.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.lamp.light.Interceptor;
import com.lamp.light.handler.Coordinate.ParametersType;
import com.lamp.light.handler.CoordinateHandler.CoordinateHandlerWrapper;
import com.lamp.light.netty.NettyClient;
import com.lamp.light.serialize.Serialize;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;

/**
 * 
 * HandleProxy <BR>
 * 1. 解析所有的请求信息，与返回基本信息 <BR>
 * 2. 把解析出来的信息，生产netty Request对象<BR>
 * 3. 创建有效的 http china <BR>
 * 4. 发送
 */
public class HandleProxy implements InvocationHandler {

    private AnnotationAnalysis annotationAnalysis = new AnnotationAnalysis();

    private Map<Method, HandleMethod> handleMethodMap = new ConcurrentHashMap<>();

    // http1.1支持
    private NettyClient nettyClient;

    private InetSocketAddress inetSocketAddress;

    private List<Interceptor> interceptorList = new ArrayList<>();

    private RequestInfo requestInfo;

    private Serialize serialize;

    public HandleProxy(String path , Class<?> proxy, InetSocketAddress inetSocketAddress, List<Interceptor> interceptorList,
        Serialize serialize) throws Exception {
        this.requestInfo = annotationAnalysis.analysis(proxy);
        this.interceptorList = interceptorList;
        this.serialize = serialize;
        this.inetSocketAddress = inetSocketAddress;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        HandleMethod handleMethod = handleMethodMap.get(method);
        if (Objects.isNull(handleMethod)) {
            handleMethod = new HandleMethod();
            handleMethod.requestInfo = annotationAnalysis.analysis(method, this.requestInfo);
        }
        RequestInfo requestInfo = handleMethod.requestInfo;
        for (Interceptor interceptor : interceptorList) {
            args = interceptor.handlerBefore(proxy, method, requestInfo, args);
        }
        CoordinateHandlerWrapper coordinateHandlerWrapper = CoordinateHandler.getCoordinateHandlerWrapper();
        // path
        if (Objects.nonNull(requestInfo.getPathList())) {
            coordinateHandlerWrapper.pathCoordinateHandler.setObject(requestInfo.getUrl());
            coordinateHandler(args, requestInfo.getPathList(), coordinateHandlerWrapper.pathCoordinateHandler);
        }
        // query
        if (Objects.nonNull(requestInfo.getQueryList())) {
            coordinateHandlerWrapper.queryCoordinateHandler.setObject(requestInfo.getUrl());
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
        DefaultFullHttpRequest defaultFullHttpRequest;
        ByteBuf buffer = Unpooled.EMPTY_BUFFER;
        if (Objects.equals(HttpMethod.POST, handleMethod.requestInfo.getHttpMethod())) {
            if (requestInfo.getIsBody()) {
                // body 协议 httpHeaders
                byte[] bytes = serialize.serialize(args[requestInfo.getBodyIndex()]);
                buffer = Unpooled.directBuffer(bytes.length).writeBytes(bytes);
            }
        }
        defaultFullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, requestInfo.getHttpMethod(),
            requestInfo.getUrl(), buffer, httpHeaders, httpHeaders);
        if (Objects.nonNull(requestInfo.getFieldList())) {
            HttpPostRequestEncoder httpPostRequestEncoder = new HttpPostRequestEncoder(defaultFullHttpRequest, true);
            coordinateHandlerWrapper.fieldCoordinateHandler.setObject(httpPostRequestEncoder);
            coordinateHandler(args, requestInfo.getFieldList(), coordinateHandlerWrapper.fieldCoordinateHandler);
        }
        AsynReturn asynReturn = new AsynReturn();
        nettyClient.write(asynReturn, inetSocketAddress);
        return null;
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
                coordinateHandler.handler(coordinate.getKey(), (String)object);
            } else if (Objects.equals(coordinate.getType(), ParametersType.MAP)) {
                Map<String, Object> map = (Map<String, Object>)object;
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

    static class HandleMethod {
        private RequestInfo requestInfo;

        private Method method;
    }
}
