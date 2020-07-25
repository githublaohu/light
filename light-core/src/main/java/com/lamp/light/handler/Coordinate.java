package com.lamp.light.handler;

import java.lang.reflect.Method;

public class Coordinate {

    private int index;

    private String key;

    private Method method;

    private ParametersType type;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ParametersType getType() {
        return type;
    }

    public void setType(ParametersType type) {
        this.type = type;
    }

    public enum ParametersType {

        BASIC, PACKING, STRING, MAP, LIST, LIST_MAP, LIST_OBJECT, OBJECT, UPLOAD,UPLOAD_LIST,UPLOAD_MAP;
    }
}
