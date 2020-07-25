package com.lamp.light;

public class LightContext {

    private static final ThreadLocal<LightContext> CONTEXT_LOCAL = new ThreadLocal<LightContext>() {
        protected LightContext initialValue() {
            return new LightContext();
        }
    };

    
    
}
