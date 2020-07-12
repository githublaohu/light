package com.lamp.light.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

import io.netty.handler.codec.http.HttpMethod;

public class AnnotationAnalysis {

    private static final Map<Class<?>, Class<?>> DATA_ANNOTION = new HashMap<>();

    private static final Map<Class<?>, Method> DATA_ANNOTION_METHOD = new HashMap<>();

    private static final Set<Class<?>> IS_PACKING = new HashSet<>();
    
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
        IS_PACKING.add(Long.class);
        IS_PACKING.add(Integer.class);
        IS_PACKING.add(Short.class);
        IS_PACKING.add(Byte.class);
        IS_PACKING.add(Double.class);
        IS_PACKING.add(Float.class);
        IS_PACKING.add(Byte.class);
        IS_PACKING.add(Boolean.class);
    }
    
    public RequestInfo analysis(Class<?> clazz) throws Exception {
        RequestInfo requestInfo = new RequestInfo();
        readHttpMethod(clazz, requestInfo);
        return requestInfo;
    }

    public RequestInfo analysis(Method method, RequestInfo classRequestInfo) throws Exception {
        Headers headers = method.getAnnotation(Headers.class);
        RequestInfo requestInfo = new RequestInfo();
        if (Objects.nonNull(headers) || Objects.nonNull(classRequestInfo.getHeader())) {
            Map<String, String> headerMap = new HashMap<>();
            if (Objects.nonNull(classRequestInfo.getHeader())) {
                headerMap.putAll(classRequestInfo.getHeader());
            }
            if (Objects.nonNull(headers)) {
                String[] values = headers.value();
                for (String value : values) {
                    String[] headerKeyValue = value.split(":");
                    headerMap.put(headerKeyValue[0], headerKeyValue[1]);
                }
            }
            requestInfo.setHeader(headerMap);
        }
       
        readHttpMethod(method, requestInfo);
        if (Objects.isNull(requestInfo.getHttpMethod())) {
            if (Objects.isNull(classRequestInfo.getHttpMethod())) {
                // 异常
            }
        }
        Parameter[] parameters = method.getParameters();
        if (Objects.nonNull(parameters) && parameters.length != 0) {
            readParameter(parameters, requestInfo);
        }

        requestInfo.setReturnClazz(method.getReturnType());
        return requestInfo;
    }

    private void readParameter(Parameter[] parameters, RequestInfo requestInfo) throws Exception {
        Map<Class<?>, List<Coordinate>> clazzMap = new HashMap<>();

        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            Class<?> clazz = parameter.getType();
            ParametersType type = getParametersType(clazz);
            Annotation[] annotations = parameter.getAnnotations();
            if (Objects.isNull(null) || annotations.length == 0) {
                // 通过方法识别，get 默认是query，post默认是 body或者 field
            }
            for (Annotation annotation : annotations) {
                Class<?> dataClazz = DATA_ANNOTION.get(annotation.annotationType());
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
                    createCoordinate(coordinateList, values[0], index, type, null);
                    continue;
                }
                
                if (Objects.equals(type, ParametersType.MAP)) {
                    if (values.length == 0) {
                        createCoordinate(coordinateList, null, index, type, null);
                    } else {
                        for (String value : values) {
                            createCoordinate(coordinateList, value, index, type, null);
                        }
                    }
                    continue;
                }
                if (Objects.equals(type, ParametersType.OBJECT)) {
                    if (values.length == 0) {
                        getCoordinateByClass(clazz, index, type, coordinateList);
                    } else {
                        for (String value : values) {
                            createCoordinate(coordinateList, value, index, ParametersType.OBJECT,
                                getMethod(value, clazz));;
                        }
                    }
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
    
    private static void getCoordinateByClass(Class<?> clazz ,int index,ParametersType type,  List<Coordinate> coordinateList) {
        
        java.lang.reflect.Field[] fields = clazz.getFields();
        for (java.lang.reflect.Field field : fields) {
            String fieldName = field.getName();
            Method method  = getMethod(fieldName, clazz);
            if (Objects.isNull(method)) {
                createCoordinate(coordinateList, fieldName, index, type, method);
            }
        }
    }
    
    private static Boolean createCoordinate(List<Coordinate> coordinateList,String key , int index,ParametersType type,Method method) {
        Coordinate coordinate = new Coordinate();
        coordinate.setIndex(index);
        coordinate.setType(type);
        coordinate.setKey(key);
        coordinate.setMethod(method);
        coordinateList.add(coordinate);
        return true;
    }
    
    private static Method getMethod(String name,Class<?> clazz) {
        String methodName =
            "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        try {
            Method method = clazz.getMethod(methodName);
            if (Objects.isNull(method)) {
                methodName =
                    "is" + name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
                method = clazz.getMethod(methodName);
            }
           return method;
        } catch (NoSuchMethodException | SecurityException e) {
           return null;
        }
    }
    
    
    private ParametersType getParametersType(Class<?> clazz) {
        if(clazz.isPrimitive()) {
            return ParametersType.BASIC;
        }
        if(IS_PACKING.contains(clazz)) {
            return ParametersType.PACKING;
        }
        if(String.class.equals(clazz)) {
            return ParametersType.STRING;
        }
        if(Map.class.isAssignableFrom(clazz)) {
            return ParametersType.MAP;
        }
        return ParametersType.OBJECT;
    }
}
