package com.lamp.light.api.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个是 通知模式
 * 一个是 主动加载模式
 * @param <K>
 * @param <V>
 */
public interface DataOperate<K, V> {

    public V get(K k);

    default List<V> get(List<K> k) {
        List<V> data = new ArrayList<>();
        k.forEach((key) -> {
            data.add(get(key));
        });
        return (List<V>) data;
    }

    default int insert(List<V> data){
        data.forEach(this::insert);
        return 1;
    }

    default int update(List<V> data){
        data.forEach(this::update);
        return 1;
    }

    default int delete(List<V> data){
        data.forEach(this::delete);
        return 1;
    }

    int fullUpdate(List<V> data);

    int insert(V data);

    default int update(V oldData, V newData){
        //this.delete(oldData);
        this.insert(newData);
        return 1;
    }

    int update(V newData);

    int delete(V data);
}
