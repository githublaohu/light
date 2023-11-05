package com.lamp.light.common.cache.local;

import java.util.ArrayList;
import java.util.List;

public interface DataOperate<K, V> {

    public V get(K k);

    default List<V> get(List<K> k) {
        List<V> data = new ArrayList<>();
        k.forEach((key) -> {
            data.add(get(key));
        });
        return (List<V>) data;
    }
}
