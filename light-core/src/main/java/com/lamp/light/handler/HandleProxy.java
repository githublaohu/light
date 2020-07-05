package com.lamp.light.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.lamp.light.Interceptor;
import com.lamp.light.annotation.Body;
import com.lamp.light.annotation.Cookie;
import com.lamp.light.annotation.Field;
import com.lamp.light.annotation.GET;
import com.lamp.light.annotation.Header;
import com.lamp.light.annotation.Headers;
import com.lamp.light.annotation.POST;
import com.lamp.light.annotation.Path;
import com.lamp.light.annotation.Query;
import com.lamp.light.handler.Coordinate.ParametersType;
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
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder.ErrorDataEncoderException;

/**
 * 
 * HandleProxy <BR>
 * 1. 解析所有的请求信息，与返回基本信息
 * 2. 把解析出来的信息，生产netty Request对象
 * 3. 创建有效的 http china
 * 4. 发送
 */
public class HandleProxy implements InvocationHandler {

    private static final Map<Class<?>, Class<?>> DATA_ANNOTION = new HashMap<>();
    private static final Map<Class<?>, Method> DATA_ANNOTION_METHOD = new HashMap<>();

    private static final ThreadLocal<CoordinateHandlerWrapper> COORDINATEHANDLER =
        new ThreadLocal<CoordinateHandlerWrapper>() {

            public CoordinateHandlerWrapper initialValue() {
                return new CoordinateHandlerWrapper();
            }
        };

    static {
        DATA_ANNOTION.put(Header.class, Header.class);
        DATA_ANNOTION.put(Cookie.class, Cookie.class);
        DATA_ANNOTION.put(Path.class, Path.class);
        DATA_ANNOTION.put(Query.class, Query.class);
        DATA_ANNOTION.put(Field.class, Field.class);
        DATA_ANNOTION.put(Body.class, Body.class);

        for (Class<?> clazz : DATA_ANNOTION.keySet()) {
            try {
                DATA_ANNOTION_METHOD.put(clazz, clazz.getMethod("value"));
            } catch (NoSuchMethodException | SecurityException e) {

            }
        }
    }

    private Map<Method, HandleMethod> handleMethodMap = new ConcurrentHashMap<>();

    // http1.1支持
    private NettyClient nettyClient;

    private List<Interceptor> interceptorList = new ArrayList<>();

    private RequestInfo requestInfo;

    private Class<?> proxy;

    private Serialize serialize;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        HandleMethod handleMethod = handleMethodMap.get(method);

        if (Objects.isNull(handleMethod)) {
            
            Headers headers = method.getAnnotation(Headers.class);
            if (Objects.nonNull(headers) || Objects.nonNull(this.requestInfo.getHeader())) {
                Map<String, String> headerMap = new HashMap<>();
                if (Objects.nonNull(this.requestInfo.getHeader())) {
                    headerMap.putAll(this.requestInfo.getHeader());
                }
                if (Objects.nonNull(headers)) {
                    String[] values = headers.value();
                    for (String value : values) {

                    }
                }
            }
            RequestInfo requestInfo = new RequestInfo();
            readHttpMethod(method, requestInfo);
            if(Objects.isNull(requestInfo.getHttpMethod())) {
                if(Objects.isNull(this.requestInfo.getHttpMethod())) {
                    // 异常
                }
                requestInfo.setHttpMethod(this.requestInfo.getHttpMethod());
            }
            Parameter[] parameters = method.getParameters();
            if(Objects.nonNull(parameters) && parameters.length == 0) {
                readParameter(parameters, requestInfo);
            }

            handleMethod = new HandleMethod();
        }
        RequestInfo requestInfo = handleMethod.requestInfo;
        for (Interceptor interceptor : interceptorList) {
            args = interceptor.handlerBefore(proxy, method, requestInfo, args);
        }
        CoordinateHandlerWrapper coordinateHandlerWrapper = COORDINATEHANDLER.get();
        // path
        
        // query
        
        // header 
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        Set<Entry<String, String>> it = requestInfo.getHeader().entrySet();
        for(Entry<String, String> e : it) {
            httpHeaders.add(e.getKey(), e.getValue());
        }
        if(Objects.nonNull(requestInfo.getHeaderList())) {
            
        }
        
        
        // HttpPostRequestEncoder 用于post请求
        DefaultFullHttpRequest defaultFullHttpRequest ;
        if(Objects.equals(HttpMethod.POST, handleMethod.requestInfo.getHttpMethod())) {
            if(requestInfo.getIsBody()) {
                // body 协议 httpHeaders
                byte[] bytes = serialize.serialize(args[requestInfo.getBodyIndex()]);
                ByteBuf buffer = Unpooled.directBuffer(bytes.length).writeBytes(bytes);
                
                defaultFullHttpRequest =  new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, requestInfo.getHttpMethod(),"",buffer);
            }else {
                defaultFullHttpRequest =  new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, requestInfo.getHttpMethod(),"");
                HttpPostRequestEncoder httpPostRequestEncoder = new HttpPostRequestEncoder(defaultFullHttpRequest, true);
                List<Coordinate> list = requestInfo.getFieldList();
                //
                // File
                // 流数据
            }
        }
        // 执行
        return null;
    }

    private void readParameter(Parameter[] parameters, RequestInfo requestInfo) throws IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Map<Class<?>, List<Coordinate>> clazzMap = new HashMap<>();

        int i = 0;
        for (Parameter parameter : parameters) {
            Class<?> clazz = parameter.getType();
            ParametersType type = getParametersType(clazz);
            Annotation[] annotations = parameter.getAnnotations();
            if (Objects.isNull(null) || annotations.length == 0) {
                // 通过方法识别，get 默认是query，post默认是 body或者 field
            }
            for (Annotation annotation : annotations) {
                Class<?> dataClazz = DATA_ANNOTION.get(annotation.getClass());
                if (Objects.isNull(dataClazz)) {
                    continue;
                }
                List<Coordinate> coordinateList = clazzMap.get(dataClazz);
                if (Objects.isNull(coordinateList)) {
                    coordinateList = new ArrayList<>();
                    clazzMap.put(dataClazz, coordinateList);
                }

                String[] values = (String[])DATA_ANNOTION_METHOD.get(dataClazz).invoke(annotation);
                if (Objects.equals(type, ParametersType.BASIC) || Objects.equals(type, ParametersType.PACKING)
                    || Objects.equals(type, ParametersType.STRING)) {
                    if (values.length == 0 || values.length > 1) {
                        // 异常
                    }
                    Coordinate coordinate = new Coordinate();
                    coordinate.setIndex(i++);
                    coordinate.setType(type);
                    coordinate.setKey(values[0]);
                    coordinateList.add(coordinate);
                    continue;
                }

                if ((Objects.equals(type, ParametersType.MAP) || Objects.equals(type, ParametersType.OBJECT))
                    && values.length == 0) {
                    Coordinate coordinate = new Coordinate();
                    coordinate.setIndex(i++);
                    coordinate.setType(type);
                    coordinateList.add(coordinate);
                }
                if (Objects.equals(type, ParametersType.MAP)) {
                    for (String value : values) {
                        Coordinate coordinate = new Coordinate();
                        coordinate.setIndex(i);
                        coordinate.setType(type);
                        coordinate.setKey(value);
                        coordinateList.add(coordinate);
                    }
                    i++;
                }
                if (Objects.equals(type, ParametersType.OBJECT)) {
                    for (String value : values) {
                        Coordinate coordinate = new Coordinate();
                        coordinate.setIndex(i);
                        coordinate.setType(type);
                        coordinate.setKey(value);
                        coordinate.setMethod(clazz.getMethod(""));
                        coordinateList.add(coordinate);
                    }
                    i++;
                }

            }
        }
    }

    private void readHttpMethod(AnnotatedElement annotatedElement, RequestInfo requestInfo) {
        // 读取http方法
        POST post = annotatedElement.getAnnotation(POST.class);
        if (Objects.nonNull(post)) {
            requestInfo.setHttpMethod(HttpMethod.POST);
            requestInfo.setUrl(post.value());
            Body body = annotatedElement.getAnnotation(Body.class);
            if (Objects.nonNull(body)) {
                requestInfo.setIsBody(true);
            }
            return;
        }
        GET get = annotatedElement.getAnnotation(GET.class);
        if (Objects.nonNull(get)) {
            requestInfo.setHttpMethod(HttpMethod.GET);
            requestInfo.setUrl(get.value());
        }

    }

    private void coordinateHandler(Object[] args , List<Coordinate> coordinateList , CoordinateHandler<Object> coordinateHandler) {
        // 要支持form的list
        if(Objects.nonNull(coordinateList)) {
            for(Coordinate coordinate :  coordinateList) {
                Object object = args[coordinate.getIndex()];
                if(Objects.equals(coordinate.getType(), ParametersType.BASIC) || Objects.equals(coordinate.getType(), ParametersType.PACKING)) {
                    coordinateHandler.handler(coordinate.getKey(), object.toString());;
                }else if(Objects.equals(coordinate.getType(), ParametersType.STRING) ) {
                    coordinateHandler.handler(coordinate.getKey(), (String)object);
                }else if(Objects.equals(coordinate.getType(), ParametersType.MAP)) {
                    Map<String , Object> map = (Map<String, Object>)object;
                    // Object 需要转换
                    if(Objects.isNull(coordinate.getKey())) {
                        for(Entry<String, Object> e :  map.entrySet()) {
                            Object value = e.getValue();
                            coordinateHandler.handler(e.getKey(), null);
                        }
                    }else {
                       // coordinateHandler.handler(coordinate.getKey(), map.get(coordinate.getKey()));
                    }
                }else if(Objects.equals(coordinate.getType(), ParametersType.OBJECT)) {
                    if(Objects.isNull(coordinate.getKey())) {
                        //JSON.toJSON(javaObject)；
                    }
                }
            }
        }
    }

    private ParametersType getParametersType(Class<?> clazz) {
        return null;
    }

    static class HandleMethod {
        private RequestInfo requestInfo;

        private Method method;
    }

    private interface CoordinateHandler<T> {

        void handler(String key, String value);
    }

    static abstract class AbstractCoordinateHandler<T> implements CoordinateHandler<T> {

        T object;

        void setObject(T object) {
            this.object = object;
        }
    }

    static class CookieCoordinateHandler extends AbstractCoordinateHandler<HttpHeaders> {
        @Override
        public void handler(String name, String value) {
            object.add(name, value);
        }
    }

    static class HeaderCoordinateHandler extends AbstractCoordinateHandler<HttpHeaders> {
        @Override
        public void handler(String name, String value) {
            object.add(name, value);
        }
    }

    static class PathCoordinateHandler extends AbstractCoordinateHandler<String> {
        @Override
        public void handler(String name, String value) {

        }
    }

    static class QueryCoordinateHandler extends AbstractCoordinateHandler<String> {
        @Override
        public void handler(String name, String value) {

        }
    }

    static class FieldCoordinateHandler extends AbstractCoordinateHandler<HttpPostRequestEncoder> {
        @Override
        public void handler(String name, String value) {
            try {
                object.addBodyAttribute(name, value);
            } catch (ErrorDataEncoderException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static class CoordinateHandlerWrapper {

        private QueryCoordinateHandler queryCoordinateHandler = new QueryCoordinateHandler();

        private FieldCoordinateHandler fieldCoordinateHandler = new FieldCoordinateHandler();

        private PathCoordinateHandler pathCoordinateHandler = new PathCoordinateHandler();

        private HeaderCoordinateHandler headerCoordinateHandler = new HeaderCoordinateHandler();

        private CookieCoordinateHandler cookieCoordinateHandler = new CookieCoordinateHandler();
    }

}
