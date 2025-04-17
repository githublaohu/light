package com.lamp.light.api.http.manufacturer;

import com.lamp.light.api.http.annotation.method.POST;

/**
 * @author hahaha
 */
public @interface Manufacturer {

    String name();

    String version() default "";

	POST post() default {};


}
