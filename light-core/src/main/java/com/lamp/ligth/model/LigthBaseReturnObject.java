package com.lamp.ligth.model;

public class LigthBaseReturnObject {

    private Boolean success = true;
    
    private Throwable throwable;
   

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
