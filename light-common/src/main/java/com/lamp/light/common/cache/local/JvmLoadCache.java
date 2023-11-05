package com.lamp.light.common.cache.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class JvmLoadCache<V> extends AbstractDataOperate<String,V>{

    private final Map<String, List<V>> cache = new ConcurrentHashMap<>();


    @Override
    public V get(String k) {
        return (V) cache.get(k);
    }

    @Override
    public int insert(V data) {
        String key = this.getKey(data,'-');
        List<V> value = cache.get(key);
        if(Objects.isNull(value)){
            synchronized (this){
                value = cache.get(key);
                if(Objects.isNull(value)) {
                    value = new CopyOnWriteArrayList<>();
                    cache.put(key, value);
                }
            }
        }
        value.add(data);

        return 0 ;
    }

    @Override
    public int delete(V data) {
        String key = this.getKey(data,'-');
        List<V> value = cache.get(key);
        if(Objects.nonNull(value)){
            value.remove(data);
        }
        return 0;
    }
}
