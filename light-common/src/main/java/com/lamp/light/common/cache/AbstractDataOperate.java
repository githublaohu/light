package com.lamp.light.common.cache;

import com.lamp.light.api.cache.CacheActionData;
import com.lamp.light.api.cache.DataOperate;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 *  可以是
 *  1. 注册服务
 *  2. 可以业务接口
 *  3. 可以是本地转换
 * @param <K>
 * @param <V>
 */
public abstract class AbstractDataOperate<K, V> implements DataOperate<K, V> {

    private CacheActionData cacheActionData;


    String getKey(V key, char segmentation) {
        try {
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < cacheActionData.getKey().length; i++) {
                String keyName = cacheActionData.getKey()[i];
                stringBuffer.append(FieldUtils.readField(key, keyName, true).toString());
                if (i < cacheActionData.getKey().length) {
                    stringBuffer.append(segmentation);
                }
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
