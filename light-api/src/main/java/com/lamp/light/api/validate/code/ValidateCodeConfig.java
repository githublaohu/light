package com.lamp.light.api.validate.code;

public class ValidateCodeConfig extends  ValidateCode{

    private long  effectiveTime;

    private boolean persistence;

    /**
     * 数字随机
     * 字母随机
     * 算法
     */
    private RandomEnum type;

    private int length;

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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(boolean uniqueness) {
        this.uniqueness = uniqueness;
    }
}
