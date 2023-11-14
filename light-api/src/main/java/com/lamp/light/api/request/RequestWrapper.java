package com.lamp.light.api.request;

import java.lang.reflect.Method;

import com.lamp.light.api.response.ReturnMode;
import com.lamp.light.api.serialize.Serialize;

public class RequestWrapper {

    private RequestInfo requestInfo;


    public RequestWrapper(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public Method method() {
        return requestInfo.getMethod();
    }

    public Object proxy() {
        return requestInfo.getProxy();
    }

    public boolean tls() {
        return requestInfo.isTls();
    }

    public ReturnMode returnMode() {
        return requestInfo.getReturnMode();
    }

    public Serialize serialize() {
        return requestInfo.getSerialize();
    }
}
