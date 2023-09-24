package com.lamp.light.api.cache.local;

import java.util.List;

public interface DataLoadService<T> {

    public List<T> load();
}
