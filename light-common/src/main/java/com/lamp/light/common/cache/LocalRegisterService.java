package com.lamp.light.common.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lamp.light.api.cache.DataOperate;

public class LocalRegisterService implements DataOperate<Object, Object> {


    private Map<Class<?>, List<DataOperate>> classDataOperateMap = new ConcurrentHashMap<>();


    private List<DataOperate> getDataOperate(Object key) {
        return classDataOperateMap.get(key.getClass());
    }

    @Override
    public Object get(Object o) {
        return null;
    }

    @Override
    public int fullUpdate(List<Object> data) {
        this.getDataOperate(data.get(0)).forEach((o) -> {
            o.fullUpdate(data);
        });
        return 0;
    }

    @Override
    public int insert(Object data) {
        this.getDataOperate(data).forEach((o) -> {
            o.insert(data);
        });
        return 0;
    }

    @Override
    public int update(Object newData) {
        return this.insert(newData);
    }

    @Override
    public int delete(Object data) {
        this.getDataOperate(data).forEach((o) -> {
            o.delete(data);
        });
        return 1;
    }
}
