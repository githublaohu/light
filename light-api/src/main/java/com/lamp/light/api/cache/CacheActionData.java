package com.lamp.light.api.cache;

/**
 * @author laohu
 */

public class CacheActionData {

    private String name;

    private String[] key;

    private long loadInterval;


    public String[] getKey() {
        return key;
    }

    public void setKey(String[] key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLoadInterval() {
        return loadInterval;
    }

    public void setLoadInterval(long loadInterval) {
        this.loadInterval = loadInterval;
    }
}
