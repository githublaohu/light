package com.lamp.light.api.data;

public enum DataType {

    INSERT, UPDATE, DELETE;


    public boolean isInsert() {
        return this == INSERT;
    }

    public boolean isUpdate() {
        return this == UPDATE;
    }

    public boolean isDelete() {
        return this == DELETE;
    }
}
