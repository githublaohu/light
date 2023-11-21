package com.lamp.light.response.execute;

import com.lamp.light.handler.AsyncReturn;

import io.netty.handler.codec.http.HttpResponse;

public interface ExecuteRevert {


    <T> T execute(HttpResponse httpResponse, AsyncReturn asyncReturn);
}
