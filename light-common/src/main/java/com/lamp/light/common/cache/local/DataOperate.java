package com.lamp.light.common.cache.local;

import java.util.List;

public interface DataOperate<K, V> {

    public V get(K k);

    public V get(List<K> k);
}
