package com.lamp.light.api.cache;

import java.util.List;

/**
 * @author laohu
 */
public interface CacheDataAction<T> {


    public default int insert(List<T> data){
        data.forEach(this::insert);
        return 1;
    }

    public int insert(T data);

    default int update(T oldData, T newData){
        this.delete(oldData);
        this.insert(newData);
        return 0;
    }
    public int delete(T data);
}
