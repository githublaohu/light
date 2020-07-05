package com.lamp.light.serialize;

public interface Serialize {

    public byte[] serialize(Object object);
    
    public <T> T deserialization(T t);
}
