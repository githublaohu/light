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

import java.util.List;

import com.lamp.light.LightContext;
import com.lamp.light.api.call.Call;
import com.lamp.light.api.interceptor.Interceptor;
import com.lamp.light.api.response.ReturnMode;
import com.lamp.light.api.serialize.Serialize;
import com.lamp.light.handler.HandlerProxy.HandleMethod;
import com.lamp.light.handler.Http11Factory.ChannelWrapper;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

public class AsyncReturn {

    private Channel channel;

    private Object[] args;

    private Object object;

    private Serialize serialize;

    private HttpRequest fullHttpRequest;

    private Integer requestTimes = 1;

    private HandleMethod handleMethod;

    private Call<Object> call;

    private ReturnMode returnMode;
    
    private LightContext lightContext;
    
    private ChannelWrapper channelWrapper;
    
    private List<Interceptor> interceptorList;

    public AsyncReturn() {

    }

    public Channel channel() {
        return channel;
    }

    public void channel(Channel channel) {
        this.channel = channel;
    }

    public Object[] getArgs() {
        return args;
    }

    public void args(Object[] args) {
        this.args = args;
    }

    public Serialize serialize() {
        return serialize;
    }

    public void serialize(Serialize serialize) {
        this.serialize = serialize;
    }

    public HttpRequest fullHttpRequest() {
        return fullHttpRequest;
    }

    public void fullHttpRequest(HttpRequest fullHttpRequest) {
        this.fullHttpRequest = fullHttpRequest;
    }

    public Integer requestTimes() {
        return requestTimes;
    }

    public void requestTimes(Integer requestTimes) {
        this.requestTimes = requestTimes;
    }

    public HandleMethod handleMethod() {
        return handleMethod;
    }

    public void handleMethod(HandleMethod handleMethod) {
        this.handleMethod = handleMethod;
    }

    public Call<Object> call() {
        return call;
    }

    public void call(Call<Object> call) {
        this.call = call;
    }

    public ReturnMode returnMode() {
        return returnMode;
    }

    public void returnMode(ReturnMode returnMode) {
        this.returnMode = returnMode;
    }

    public synchronized void setObject(Object object) {
        this.object = object;
        this.notify();
    }

    public synchronized Object getObject() throws InterruptedException {
        this.wait(requestTimes + 100);
        if(object instanceof Throwable) {
            throw new RuntimeException((Throwable)object);
        }
        return object;
    }
    
    public void lightContext(LightContext lightContext) {
    	this.lightContext = lightContext;
    }
    
    public LightContext lightContext() {
    	return this.lightContext;
    }

	public ChannelWrapper channelWrapper() {
		return channelWrapper;
	}

	public void channelWrapper(ChannelWrapper channelWrapper) {
		this.channelWrapper = channelWrapper;
	}
    
    public void  interceptorList (List<Interceptor> interceptorList) {
    	this.interceptorList = interceptorList;
    }
    
    public List<Interceptor>  interceptorList () {
    	return this.interceptorList;
    }
}
