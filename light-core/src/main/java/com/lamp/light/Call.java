package com.lamp.light;

import com.lamp.light.response.Response;

public interface Call<T> {

    public Response<T> execute();
    
    public void execute(Callback<T> callback);
    
    boolean isExecuted();

    void cancel();

    boolean isCanceled();
}
