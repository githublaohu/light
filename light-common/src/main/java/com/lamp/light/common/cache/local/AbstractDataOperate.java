package com.lamp.light.common.cache.local;

import com.lamp.light.api.cache.CacheActionData;
import com.lamp.light.api.cache.CacheDataAction;
import org.apache.commons.lang3.reflect.FieldUtils;

public abstract class AbstractDataOperate<K,V> implements  DataOperate<K,V> ,CacheDataAction<V> {

    private CacheActionData cacheActionData;


    String getKey(V key,char segmentation){
        try {
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < cacheActionData.getKey().length; i++) {
                String keyName = cacheActionData.getKey()[i];
                stringBuffer.append(FieldUtils.readField(key, keyName, true).toString());
                if( i < cacheActionData.getKey().length){
                    stringBuffer.append(segmentation);
                }
            }
            return stringBuffer.toString();
        }catch (Exception e){
            throw  new RuntimeException( e);
        }
    }

}
