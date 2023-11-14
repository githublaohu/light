package com.lamp.light.api.manufacturer;

public @interface Manufacturer {

    String name();

    String product();

    String version() default "";


}
