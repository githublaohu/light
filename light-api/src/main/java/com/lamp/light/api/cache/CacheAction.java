package com.lamp.light.api.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author laohu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheAction {

    String[] keys();

    String name();

    long loadInterval() default 1000 * 60 * 60;

    boolean persistence() default false;

    String registerType() default "register";
}
