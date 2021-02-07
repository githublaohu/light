package com.lamp.light.handler;

import com.lamp.light.response.ReturnMode;
import com.lamp.light.serialize.Serialize;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestInfo {

    private String url;

    private HttpMethod httpMethod;

    private boolean isBody;

    private Long requestTimeout;

    private Serialize serialize;

    private Type returnClazz;

    private Map<String, String> header = new HashMap<>();

    private List<Coordinate> headerList;

    private List<Coordinate> queryList;

    private List<Coordinate> pathList;

    private List<Coordinate> fieldList;

    private List<Coordinate> cookieList;

    private int bodyIndex;

    private Method method;
    
    private ReturnMode returnMode;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsBody() {
        return isBody;
    }

    public void setIsBody(Boolean isBody) {
        this.isBody = isBody;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getBodyIndex() {
        return bodyIndex;
    }

    public void setBodyIndex(int bodyIndex) {
        this.bodyIndex = bodyIndex;
    }

    public Type getReturnClazz() {
        return returnClazz;
    }

    public void setReturnClazz(Type returnClazz) {
        this.returnClazz = returnClazz;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public List<Coordinate> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<Coordinate> headerList) {
        this.headerList = headerList;
    }

    public List<Coordinate> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<Coordinate> queryList) {
        this.queryList = queryList;
    }

    public List<Coordinate> getPathList() {
        return pathList;
    }

    public void setPathList(List<Coordinate> pathList) {
        this.pathList = pathList;
    }

    public List<Coordinate> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<Coordinate> fieldList) {
        this.fieldList = fieldList;
    }

    public List<Coordinate> getCookieList() {
        return cookieList;
    }

    public void setCookieList(List<Coordinate> cookieList) {
        this.cookieList = cookieList;
    }

    public Serialize getSerialize() {
        return serialize;
    }

    public void setSerialize(Serialize serialize) {
        this.serialize = serialize;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ReturnMode getReturnMode() {
        return returnMode;
    }

    public void setReturnMode(ReturnMode returnMode) {
        this.returnMode = returnMode;
    }

    public void setBody(boolean isBody) {
        this.isBody = isBody;
    }

    
    

}
