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
package com.lamp.light.api.interceptor;

import com.lamp.light.api.request.RequestWrapper;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface Interceptor {

    public default Object[] handlerBefore( RequestWrapper requestWrapper , Object[] args) {
    	return args;
    }
    
    public default HttpRequest handlerRequest( RequestWrapper requestWrapper,HttpRequest defaultFullHttpRequest) {
    	return defaultFullHttpRequest;
    }
    
    public default void handlerErrer(Throwable throwable,  RequestWrapper requestWrapper , Object[] args) {
    	
    }
    
    public default void handlerResponse(HttpResponse defaultHttpResponse) { }
    
    public default void handlerAfter(RequestWrapper requestWrapper,HttpResponse defaultHttpResponse) {
    	
    }
}
