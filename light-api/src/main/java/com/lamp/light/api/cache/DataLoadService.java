package com.lamp.light.api.cache;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.lamp.light.api.data.channel.model.DataLoadRequest;

/**
 * 加载模式： 1. 主动加载。 把被动记在模拟称主动加载 2. 被动加载
 * <p>
 * 数据增信方式 1. 全量更新 2. 增量情况下 批量更新 3. 增量情况下 单挑更新
 *
 *  可以同步多个 服务？
 *  一个 DataLoadService 对应 一个 DataOperate 还是 多个 DataOperate？
 * Dubbo 如果进行加载
 * 1. 一个dubbo 远程加载 ， 一个 DataLoadService
 * 2. 获得所有的 DataLoadService 的接口，实现 T
 */
public interface DataLoadService<T> {

    CompletableFuture<List<T>> fullLoad(DataLoadRequest dataLoadRequest);

    CompletableFuture<IncrementLoadResult<T>> incrementLoad(DataLoadRequest dataLoadRequest);
}
