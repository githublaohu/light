package com.lamp.light.api.http.interceptor;

public @interface  InterceptorLabel {

	String manufacturer();
	
	String version() ;
	
	boolean defaultVersion() default false;
}
