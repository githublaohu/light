package com.lamp.light.api.data.channel.model;

import java.util.List;

public class IncrementLoadResult<V> {

    public boolean isIncrement() {
        return increment;
    }

    public void setIncrement(boolean increment) {
        this.increment = increment;
    }

    private boolean increment = true;

    private List<V> insert;

    private List<V> update;

    private List<V> delete;

    public List<V> getInsert() {
        return insert;
    }

    public void setInsert(List<V> insert) {
        this.insert = insert;
    }

    public List<V> getUpdate() {
        return update;
    }

    public void setUpdate(List<V> update) {
        this.update = update;
    }

    public List<V> getDelete() {
        return delete;
    }

    public void setDelete(List<V> delete) {
        this.delete = delete;
    }
}
