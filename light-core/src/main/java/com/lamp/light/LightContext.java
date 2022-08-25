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
package com.lamp.light;

import java.util.HashMap;
import java.util.Map;

import com.lamp.light.api.response.Response;

public class LightContext {

	private static final ThreadLocal<LightContextWrapper> CONTEXT_LOCAL = new ThreadLocal<LightContextWrapper>() {
        protected LightContextWrapper initialValue() {
            return new LightContextWrapper();
        }
    };

    
    public static LightContext lightContext() {
    	return CONTEXT_LOCAL.get().lightContext;
    }
    
    public static void remove() {
    	CONTEXT_LOCAL.get().lightContext = new LightContext();
    }
    
    public static void lightContext(LightContext lightContext) {
    	CONTEXT_LOCAL.get().lightContext = lightContext;
    }
    
    private Map<String,Object> attachments = new HashMap<>();
    
    private Throwable throwable;
    
    private Object result;
    
    private Boolean success;
    
    private Response<Object> response;
    
    
    public void setAttachments(String key ,Object value) {
    	attachments.put(key, value);
    }
   
    @SuppressWarnings("unchecked")
	public <T>T getAttachments(String key){
    	return (T) attachments.get(key);
    }
    
    public void throwable(Throwable throwable,Response<Object> response) {
    	this.response = response;
    	this.throwable = throwable;
    	this.success = false;
    }
    
    public Throwable throwable() {
    	return this.throwable;
    } 
    
    public Boolean isSuccess() {
    	return this.success;
    }
    
    public void result(Object result,Response<Object> response) {
    	this.result = result;
    	this.success = true;
    	this.response = response;
    }
    
    @SuppressWarnings("unchecked")
	public <T>T result(){
    	return (T)this.result;
    }
    
    @SuppressWarnings("unchecked")
	public <T>Response<T> response(){
    	return (Response<T>)this.response;
    }
    
    public void clear() {
    	this.attachments.clear();
    	this.throwable = null;
    	this.response = null;
    	this.result = null;
    }
    
    static class LightContextWrapper {
    	private LightContext lightContext = new LightContext();
    }
}
