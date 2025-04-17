package com.lamp.ligth.unify.api.cloud.api;

import com.lamp.light.api.request.RequestWrapper;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author laohu
 */
public interface AuthenticationHandler {


    void encrypt(CloudConfig cloudConfig , RequestWrapper requestWrapper, HttpRequest request);


    void decrypt(CloudConfig cloudConfig , HttpResponse response);

}
