package com.lamp.light.common.cache.local;

import com.lamp.light.api.cache.CacheAction;
import com.lamp.light.api.cache.CacheActionData;
import com.lamp.light.api.cache.DataRelationship;
import com.lamp.light.api.cache.local.CompleteLoadService;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * 数据输入与JvmLoadCache进行版本
 * 1. 通过数据库加载
 * 2. 通过RPC加载
 * 3. 通过ETCD,zk加载
 *
 * @author laohu
 */
public class CacheLocalManager {


    private final Map<String , CompleteLoadService<?>> completeLoadServiceList = new ConcurrentHashMap<>();


    private final Map<String, JvmLoadCache> jvmLoadCacheMap = new ConcurrentHashMap<>();

    private final Map<Class<?>, Map<String, Field>> classFieldMap = new ConcurrentHashMap<>();

    private final ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(48) ;


    public void register(CompleteLoadService<?> completeLoadService,long loadInterval){
        CacheAction cacheAction = completeLoadService.getClass().getAnnotation(CacheAction.class);
        if(Objects.isNull(cacheAction)){
            throw new RuntimeException(completeLoadService.getClass().getName());
        }
        CacheActionData cacheActionData = new CacheActionData();
        cacheActionData.setKey(cacheAction.keys());
        cacheActionData.setName(cacheAction.name());
        cacheActionData.setLoadInterval(loadInterval > 0? loadInterval : cacheAction.loadInterval());

    }

    public <T> T get(String name, String key, List<DataRelationship> dataRelationshipList) {

        JvmLoadCache jvmLoadCache = jvmLoadCacheMap.get(name);

        List<Object> values = (List<Object>) jvmLoadCache.get(key);

        if (Objects.isNull(dataRelationshipList)) {
            return (T) new ArrayList<>(values);
        }
        if (values.isEmpty()) {
            return (T) new ArrayList();
        }

        try {
            for (DataRelationship dataRelationship : dataRelationshipList) {
                jvmLoadCache = jvmLoadCacheMap.get(dataRelationship.getRelationshipName());
                Map<String, Field> fieldMap = classFieldMap.get(values.get(0).getClass());
                if (Objects.isNull(fieldMap)) {
                    fieldMap =
                            classFieldMap.computeIfAbsent(values.get(0).getClass(), (k) -> new ConcurrentHashMap<>());
                }

                Field field = fieldMap.get(dataRelationship.getRelationshipNode());
                List<Object> newValues = new ArrayList<>();
                for (Object v : values) {
                    Object f = FieldUtils.readField(field, v, true);
                    if (Objects.isNull(f)) {
                        continue;
                    }
                    newValues.add(jvmLoadCache.get(f.toString()));
                }
                values = newValues;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return (T)values;
    }


}
