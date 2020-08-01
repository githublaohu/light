package com.lamp.light.handler;

import java.net.InetSocketAddress;

import com.lamp.light.Call;
import com.lamp.light.Callback;
import com.lamp.light.netty.NettyClient;
import com.lamp.light.response.Response;
import com.lamp.light.response.ReturnMode;


public class DefaultCall<T>  implements Call<T> {

    private AsynReturn asynReturn;
    
    private NettyClient nettyClient;
    
    private Callback<T> callback;
    
    private Response<T> response;
    
    private InetSocketAddress inetSocketAddress;
    
    private Throwable throwable;
    
    
    public DefaultCall(AsynReturn asynReturn, NettyClient nettyClient,InetSocketAddress inetSocketAddress ) {
        this.asynReturn = asynReturn;
        this.nettyClient = nettyClient;
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public Response<T> execute() {
        nettyClient.write(asynReturn, inetSocketAddress);
        asynReturn.setReturnMode(ReturnMode.CALL_SYNS);
        return null;
    }

    @Override
    public void execute(Callback<T> callback) {
        this.callback = callback;
        nettyClient.write(asynReturn, inetSocketAddress);
        asynReturn.setReturnMode(ReturnMode.CALL_ASYNS);
        
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {
        
    }

    @Override
    public boolean isCanceled() {
        return false;
    }
    

    public Callback<T> getCallback() {
        return callback;
    }

    public Response<T> getResponse() {
        return response;
    }

    public void setResponse(Response<T> response) {
        this.response = response;
    }
    
    
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
    
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public void throwThrowable() {
        throw new RuntimeException(throwable);
    }

    @Override
    public boolean isSuccess() {
        return throwable == null;
    }

    
    
}
