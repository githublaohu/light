package com.lamp.light.api.data.sync;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.lamp.light.api.cache.IncrementLoadResult;
import com.lamp.light.api.data.channel.model.DataLoadRequest;

public interface SyncDataServices<T> {


    CompletableFuture<List<T>> fullLoad(DataLoadRequest dataLoadRequest);

    CompletableFuture<IncrementLoadResult<T>> incrementLoad(DataLoadRequest dataLoadRequest);
}
