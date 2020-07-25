package com.lamp.light.response;

import com.lamp.light.serialize.Serialize;

import io.netty.handler.codec.http.HttpResponse;

// 适配 HttpResponse
public class Response<T> {

    private Serialize serialize;
    
    public Response(HttpResponse httpResponse, Serialize serialize) {
        this.serialize = serialize;
    }
}
