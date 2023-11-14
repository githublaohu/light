package com.lamp.light.api.validate.code;

public class ValidateCode {


    private String name;

    /**
     * 那个系统
     */
    private String system;

    /**
     * 那个域
     */
    private String domain;

    /**
     * 那个业务点
     */
    private String drop;

    /**
     * 对象
     */
    private String object;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

