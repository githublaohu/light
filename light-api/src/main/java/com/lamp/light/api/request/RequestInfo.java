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
package com.lamp.light.api.request;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lamp.light.api.LightConstant;
import com.lamp.light.api.response.ReturnMode;
import com.lamp.light.api.serialize.Serialize;
import com.lamp.light.api.utils.StringReplace;

import io.netty.handler.codec.http.HttpMethod;

public class RequestInfo {

    private String url;
    
    private String protocol = LightConstant.PROTOCOL_HTTP_10;

    private HttpMethod httpMethod;

    private boolean isBody;

    private Long requestTimeout;

    private Serialize serialize;

    private Type returnClazz;

    private Map<String, String> header = new HashMap<>();

    private List<Coordinate> headerList;

    private List<Coordinate> queryList;

    private List<Coordinate> pathList;
    
    private StringReplace stringReplace;

    private List<Coordinate> fieldList;

    private List<Coordinate> cookieList;
    
    private List<Coordinate> multipartList;

    private int bodyIndex;

    private Method method;
    
    private ReturnMode returnMode;
    
    private boolean isTls;
    
    private Object proxy;
    
    private RequestWrapper requestWrapper = new RequestWrapper(this);

    public RequestWrapper requestWrapper() {
    	return requestWrapper;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsBody() {
        return isBody;
    }

    public void setIsBody(Boolean isBody) {
        this.isBody = isBody;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getBodyIndex() {
        return bodyIndex;
    }

    public void setBodyIndex(int bodyIndex) {
        this.bodyIndex = bodyIndex;
    }

    public Type getReturnClazz() {
        return returnClazz;
    }

    public void setReturnClazz(Type returnClazz) {
        this.returnClazz = returnClazz;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public List<Coordinate> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<Coordinate> headerList) {
        this.headerList = headerList;
    }

    public List<Coordinate> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<Coordinate> queryList) {
        this.queryList = queryList;
    }

    public List<Coordinate> getPathList() {
        return pathList;
    }

    public void setPathList(List<Coordinate> pathList) {
        this.pathList = pathList;
    }

    public List<Coordinate> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<Coordinate> fieldList) {
        this.fieldList = fieldList;
    }

    public List<Coordinate> getCookieList() {
        return cookieList;
    }

    public void setCookieList(List<Coordinate> cookieList) {
        this.cookieList = cookieList;
    }

    public Serialize getSerialize() {
        return serialize;
    }

    public void setSerialize(Serialize serialize) {
        this.serialize = serialize;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ReturnMode getReturnMode() {
        return returnMode;
    }

    public void setReturnMode(ReturnMode returnMode) {
        this.returnMode = returnMode;
    }

    public void setBody(boolean isBody) {
        this.isBody = isBody;
    }

	public boolean isTls() {
		return isTls;
	}

	public void setTls(boolean isTls) {
		this.isTls = isTls;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public List<Coordinate> getMultipartList() {
		return multipartList;
	}

	public void setMultipartList(List<Coordinate> multipartList) {
		this.multipartList = multipartList;
	}

	public StringReplace getStringReplace() {
		return stringReplace;
	}

	public void setStringReplace(StringReplace stringReplace) {
		this.stringReplace = stringReplace;
	}

	public Object getProxy() {
		return proxy;
	}

	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}    
	
}
