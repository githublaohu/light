/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PubL v2.
 *You can use this software according to the terms and conditions of the Mulan PubL v2.
 *You may obtain a copy of Mulan PubL v2 at:
 *         http://license.coscl.org.cn/MulanPubL-2.0
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PubL v2 for more details.
 */
package com.lamp.light.handler;

import java.net.InetSocketAddress;

import com.lamp.light.api.call.Call;
import com.lamp.light.api.call.Callback;
import com.lamp.light.api.response.Response;
import com.lamp.light.api.response.ReturnMode;
import com.lamp.light.netty.NettyClient;


public class DefaultCall<T>  implements Call<T> {

    private AsyncReturn asyncReturn;
    
    private NettyClient nettyClient;
    
    private Callback<T> callback;
    
    private Response<T> response;
    
    private InetSocketAddress inetSocketAddress;
    
    private Throwable throwable;
    
    private T object;
    
    
    public DefaultCall(AsyncReturn asyncReturn, NettyClient nettyClient, InetSocketAddress inetSocketAddress ) {
        this.asyncReturn = asyncReturn;
        this.nettyClient = nettyClient;
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public Response<T> execute() {
        nettyClient.write(asyncReturn, inetSocketAddress);
        asyncReturn.returnMode(ReturnMode.CALL_SYNS);
        return null;
    }

    @Override
    public void execute(Callback<T> callback) {
    	asyncReturn.returnMode(ReturnMode.CALL_ASYNS);
        this.callback = callback;
        
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

    public void setObject(T object) {
    	this.object = object;
    }

	@Override
	public T getObject() {
		return this.object;
	}
    
}
