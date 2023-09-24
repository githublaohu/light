package com.lamp.light.common.cache.local;

import java.util.List;

public interface Data<T> {

    public default int install(List<T> data){
        data.forEach(this::install);
        return 1;
    }

    public int install(T data);

    public int update(T oldData, T newData);

    public int delete(T data);
}
