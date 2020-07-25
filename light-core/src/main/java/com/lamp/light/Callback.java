package com.lamp.light;

public interface Callback<T> {

    public void onResponse(Call<T> call, Object[] args, T returnData);

    public void onFailure(Call<T> call, Object[] args, Throwable t);
}
