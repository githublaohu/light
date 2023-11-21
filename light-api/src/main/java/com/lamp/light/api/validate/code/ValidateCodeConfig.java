package com.lamp.light.api.validate.code;

public class ValidateCodeConfig extends ValidateCode {

    private String cacheName;

    private long effectiveTime;

    private boolean persistence;

    /**
     * 数字随机
     * 字母随机
     * 算法
     */
    private RandomEnum type;


    private boolean uniqueness;


    public long getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    public RandomEnum getType() {
        return type;
    }

    public void setType(RandomEnum type) {
        this.type = type;
    }


    public boolean isUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(boolean uniqueness) {
        this.uniqueness = uniqueness;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }
}
