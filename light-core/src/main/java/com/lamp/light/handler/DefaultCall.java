package com.lamp.light.handler;

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
    
    
    public DefaultCall(AsynReturn asynReturn, NettyClient nettyClient) {
        this.asynReturn = asynReturn;
        this.nettyClient = nettyClient;
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isCanceled() {
        // TODO Auto-generated method stub
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

    
    
}
