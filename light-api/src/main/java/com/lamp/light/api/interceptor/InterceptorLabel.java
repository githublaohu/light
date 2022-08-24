package com.lamp.light.api.interceptor;

public @interface  InterceptorLabel {

	String manufacturer();
	
	String version() ;
	
	boolean defaultVersion() default false;
}
