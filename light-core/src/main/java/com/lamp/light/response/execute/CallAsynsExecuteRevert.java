package com.lamp.light.response.execute;

import com.lamp.light.handler.AsyncReturn;

import io.netty.handler.codec.http.HttpResponse;

public class CallAsynsExecuteRevert extends AbstractExecuteRevert {

    @Override
    public <T> T execute(HttpResponse httpResponse, AsyncReturn asyncReturn) {
        return null;
    }

}
