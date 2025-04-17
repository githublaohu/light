package com.lamp.ligth.unify.api.cloud;

import com.lamp.ligth.unify.api.cloud.api.CloudConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1. name  Cloud config
 * 2. 不同业务不同配置
 * 3. 业务线 -> cloud -> 接口类型 -> cloud name
 *                             -> 默认
 * 怎么识别配置
 * 业务线，可以从令牌识别，出口网管用。正常应该是？
 * 云怎么识别
 * 配置识别管理器
 * 1. 先用 name 得到configConfig
 * 2. 用 接口 类型 得到 Cloud config
 * 3.
 * @author laohu
 */
public class CloudConfigManager {

    private Map<String, CloudConfig> cloudConfigMap = new ConcurrentHashMap<>();

    /**
     * 业务类型，配置
     */
    private Map<String/** 业务类型 **/,CloudConfig> typeConfigMap = new ConcurrentHashMap<>();

}
