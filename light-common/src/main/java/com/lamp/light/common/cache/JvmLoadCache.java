package com.lamp.light.common.cache;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 1. 不支持十万基本的数据
 * 2. 不支持 负载关系
 *
 *
 * 1. 高频更新 方式
 * 2. 低频更新 方式
 *
 * 一对一，
 * 一对多模式
 *  1. 不支持大量数据
 * 关联行为：
 * 1.  A 实体 关联 B实体
 * 2. 通过 A 实体找到 B 实体
 *
 * 关联，是及时加载，还是懒加载。
 * @param <V>
 */
public class JvmLoadCache<V> extends AbstractDataOperate<String, V> {

    /**
     * 先完成 一 队 一
     */
    private final Map<String, List<V>> cache = new ConcurrentHashMap<>();


    @Override
    public V get(String k) {
        return (V) cache.get(k);
    }

    @Override
    public int fullUpdate(List<V> data) {
        if (data.isEmpty()) {
            return 1;
        }
        Map<String, V> cache = new ConcurrentHashMap<>();
        data.forEach( (v )->{
            cache.put(this.getKey(v,'-'),v);
        });
        return 1;
    }

    @Override
    public int install(V data) {
        return 0;
    }

    @Override
    public int update(V oldData, V newData) {
        return 0;
    }

    @Override
    public int update(V newData) {
        return 0;
    }

    @Override
    public int insert(V data) {
        String key = this.getKey(data, '-');
        List<V> value = cache.get(key);
        if (Objects.isNull(value)) {
            synchronized (this) {
                value = cache.get(key);
                if (Objects.isNull(value)) {
                    value = new CopyOnWriteArrayList<>();
                    cache.put(key, value);
                }
            }
        }
        value.add(data);

        return 0;
    }

    @Override
    public int delete(V data) {
        String key = this.getKey(data, '-');
        List<V> value = cache.get(key);
        if (Objects.nonNull(value)) {
            value.remove(data);
        }
        return 0;
    }
}
