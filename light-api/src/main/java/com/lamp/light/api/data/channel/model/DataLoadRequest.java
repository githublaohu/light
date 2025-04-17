package com.lamp.light.api.data.channel.model;

import java.time.LocalDateTime;

public class DataLoadRequest {

    private LocalDateTime lastFullTime;


    public Object getDataLoadParameter() {
        return dataLoadParameter;
    }

    public void setDataLoadParameter(Object dataLoadParameter) {
        this.dataLoadParameter = dataLoadParameter;
    }

    private Object dataLoadParameter;


    public LocalDateTime getLastFullTime() {
        return lastFullTime;
    }

    public void setLastFullTime(LocalDateTime lastFullTime) {
        this.lastFullTime = lastFullTime;
    }
}
