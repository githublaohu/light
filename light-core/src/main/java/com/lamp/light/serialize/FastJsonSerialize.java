package com.lamp.light.serialize;

import com.alibaba.fastjson.JSON;

public class FastJsonSerialize implements Serialize{

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialization(Class<T> t , byte[] data) {
        return JSON.parseObject(data, t);
    }

}
