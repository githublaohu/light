package com.lamp.light.common.cache;

import java.util.List;

public class RedisDataOperate  <V> extends AbstractDataOperate<String, V> {


    @Override
    public V get(String s) {
        return null;
    }

    @Override
    public int fullUpdate(List<V> data) {
        return 0;
    }

    @Override
    public int insert(V data) {
        return 0;
    }

    @Override
    public int update(V newData) {
        return 0;
    }

    @Override
    public int delete(V data) {
        return 0;
    }
}
