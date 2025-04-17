package com.lamp.light.api.data.monitor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.lamp.light.api.cache.IncrementLoadResult;
import com.lamp.light.api.data.sync.SyncDataServices;

public class SyncMonitorServices extends AbstractMonitorServices{

    private SyncDataServices<Object> syncDataServices;

    @Override
    public void fullLoad() {
        CompletableFuture<List<Object>> completableFuture=  syncDataServices.fullLoad(null);
    }

    @Override
    public void incrementLoad() {
        CompletableFuture<IncrementLoadResult<Object>> completableFuture=  syncDataServices.incrementLoad(null);
    }
}
