package com.lamp.light.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.lamp.light.Call;
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
import com.lamp.light.response.ReturnMode;
import com.lamp.light.serialize.FastJsonSerialize;
import com.lamp.light.serialize.Serialize;
import com.lamp.light.util.BaseUtils;

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
        readHeader(clazz, requestInfo, null);
        readHttpMethod(clazz, requestInfo);
        return requestInfo;
    }

    public RequestInfo analysis(Method method, RequestInfo classRequestInfo) throws Exception {
        RequestInfo requestInfo = new RequestInfo();
        readHeader(method, requestInfo, classRequestInfo);

        readHttpMethod(method, requestInfo);
        if (Objects.isNull(requestInfo.getHttpMethod())) {
            if (Objects.isNull(classRequestInfo.getHttpMethod())) {
                // 异常
            }
            requestInfo.setUrl(classRequestInfo.getUrl() + "/" + requestInfo.getMethod().getName());
        } else {
            if (Objects.nonNull(classRequestInfo.getHttpMethod())) {
                requestInfo
                    .setUrl(BaseUtils.setSlash(classRequestInfo.getUrl()) + BaseUtils.setSlash(requestInfo.getUrl()));
            }
        }
        Parameter[] parameters = method.getParameters();
        if (Objects.nonNull(parameters) && parameters.length != 0) {
            readParameter(parameters, requestInfo);
        }
        Class<?> returnType = method.getReturnType();
        
        if(Void.class.equals(returnType)) {
            
        }
        
        if(Call.class.isAssignableFrom(returnType)) {
            requestInfo.setReturnMode(ReturnMode.CALL);
            Type returnTypes = BaseUtils.getParameterUpperBound(0, (ParameterizedType)method.getGenericReturnType());
        }else {
            requestInfo.setReturnMode(ReturnMode.SYNS);
        }
        
        requestInfo.setReturnClazz(returnType);
        return requestInfo;
    }

    private void readHeader(AnnotatedElement annotatedElement, RequestInfo requestInfo, RequestInfo classRequestInfo) {
        Headers headers = annotatedElement.getAnnotation(Headers.class);
        if (Objects.nonNull(headers)
            || (Objects.nonNull(classRequestInfo) && Objects.nonNull(classRequestInfo.getHeader()))) {
            Map<String, String> headerMap = new HashMap<>();
            if (Objects.nonNull(classRequestInfo) && Objects.nonNull(classRequestInfo.getHeader())) {
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
    }

    private void readParameter(Parameter[] parameters, RequestInfo requestInfo) throws Exception {
        Map<Class<?>, List<Coordinate>> clazzMap = new HashMap<>();

        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            Class<?> clazz = parameter.getType();
            
            Annotation[] annotations = parameter.getAnnotations();
            if (Objects.isNull(null) || annotations.length == 0) {
                // 通过方法识别，get 默认是query，post默认是 body或者 field
            }
            for (Annotation annotation : annotations) {
                Class<?> dataClazz = DATA_ANNOTION.get(annotation.annotationType());
                ParametersType type = getParametersType(clazz,dataClazz);
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
        requestInfo.setHeaderList(clazzMap.get(Header.class));
        requestInfo.setPathList(clazzMap.get(Path.class));
        requestInfo.setQueryList(clazzMap.get(Query.class));
        requestInfo.setFieldList(clazzMap.get(Field.class));
    }

    private void readHttpMethod(AnnotatedElement annotatedElement, RequestInfo requestInfo)
        throws InstantiationException, IllegalAccessException {
        // 读取http方法
        POST post = annotatedElement.getAnnotation(POST.class);
        if (Objects.nonNull(post)) {
            requestInfo.setHttpMethod(HttpMethod.POST);
            requestInfo.setUrl(post.value());
            Body body = annotatedElement.getAnnotation(Body.class);
            if (Objects.nonNull(body)) {
                requestInfo.setIsBody(true);
                Class<?> serializeClass = body.getClass();
                Serialize serialize = null;
                if (serializeClass.equals(FastJsonSerialize.class)) {
                } else {
                    serialize = (Serialize)body.getClass().newInstance();
                }
                requestInfo.setSerialize(serialize);
            }
            return;
        }
        GET get = annotatedElement.getAnnotation(GET.class);
        if (Objects.nonNull(get)) {
            requestInfo.setHttpMethod(HttpMethod.GET);
            requestInfo.setUrl(get.value());
        }

    }

    private static void getCoordinateByClass(Class<?> clazz, int index, ParametersType type,
        List<Coordinate> coordinateList) {

        java.lang.reflect.Field[] fields = clazz.getFields();
        for (java.lang.reflect.Field field : fields) {
            String fieldName = field.getName();
            Method method = getMethod(fieldName, clazz);
            if (Objects.isNull(method)) {
                createCoordinate(coordinateList, fieldName, index, type, method);
            }
        }
    }

    private static Boolean createCoordinate(List<Coordinate> coordinateList, String key, int index, ParametersType type,
        Method method) {
        Coordinate coordinate = new Coordinate();
        coordinate.setIndex(index);
        coordinate.setType(type);
        coordinate.setKey(key);
        coordinate.setMethod(method);
        coordinateList.add(coordinate);
        return true;
    }

    private static Method getMethod(String name, Class<?> clazz) {
        String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        try {
            Method method = clazz.getMethod(methodName);
            if (Objects.isNull(method)) {
                methodName = "is" + name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
                method = clazz.getMethod(methodName);
            }
            return method;
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    private ParametersType getParametersType(Class<?> clazz,Class<?> dataClazz) {
        if (clazz.isPrimitive()) {
            return ParametersType.BASIC;
        }
        if (IS_PACKING.contains(clazz)) {
            return ParametersType.PACKING;
        }
        if (String.class.equals(clazz)) {
            return ParametersType.STRING;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return ParametersType.MAP;
        }
        return ParametersType.OBJECT;
    }
}
