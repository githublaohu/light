package com.lamp.light;

import com.lamp.light.response.Response;

public interface Call<T> {

    public Response<T> execute();
    
    public void execute(Callback<T> callback);
    
    public void throwThrowable();
    
    public Throwable getThrowable();
    
    public boolean isSuccess();
    
    boolean isExecuted();

    void cancel();

    boolean isCanceled();
}
