/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PubL v2.
 *You can use this software according to the terms and conditions of the Mulan PubL v2.
 *You may obtain a copy of Mulan PubL v2 at:
 *         http://license.coscl.org.cn/MulanPubL-2.0
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PubL v2 for more details.
 */
package com.lamp.light;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.lamp.light.Light.Builder;
import com.lamp.light.api.call.Call;
import com.lamp.light.api.call.Callback;
import com.lamp.light.api.http.annotation.method.POST;
import com.lamp.light.api.http.annotation.parameter.Body;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


@SuppressWarnings("restriction")
public class ExecuteTest {


    private ReturnObject returnObject = new ReturnObject("key", "value");

    private TestCallExecuteService testExecuteService;

    private TestAsyncExecuteService testAsyncExecuteService;


    @Before
    public void init() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/", new HttpHandler() {

            @Override
            public void handle(HttpExchange exchange) throws IOException {
                OutputStream os = exchange.getResponseBody();
                if (exchange.getRequestURI().getPath().endsWith("Fail")) {
                    exchange.sendResponseHeaders(404, 0);
                } else {
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseHeaders().set("Content", "application/json");

                    ReturnObject returnObject = new ReturnObject("key", exchange.getRequestURI().toString());
                    os.write(JSON.toJSONBytes(returnObject));
                }
                os.close();
            }

        });
        server.start();

        Builder builder = Light.Builder();
        Light light = builder.host("127.0.0.1").port(8001).build();
        testExecuteService = light.create(TestCallExecuteService.class);
        testAsyncExecuteService = light.create(TestAsyncExecuteService.class, new TestExecuteServiceImpl());
    }


    @Test
    public void testCall() throws InterruptedException {
        Callback<ReturnObject> callback = new Callback<ReturnObject>() {
            @Override
            public void onResponse(Call<ReturnObject> call, Object[] args, ReturnObject returnData) {
                System.out.println(JSON.toJSONString(returnData));
                Assert.assertEquals(returnData.getValue(), "/callSuccess");

            }

            @Override
            public void onFailure(Call<ReturnObject> call, Object[] args, Throwable t) {
                System.out.println(t.getMessage());
                Assert.assertEquals(t.getMessage(), "404 Not Found");

            }
        };
        testExecuteService.callSuccess(returnObject).execute(callback);
        testExecuteService.callFail(returnObject).execute(callback);
        Thread.sleep(50);
    }

    @Test
    public void testAsync() throws InterruptedException {
        testAsyncExecuteService.asyncFail(returnObject);
        testAsyncExecuteService.asyncSuccess(returnObject);
        Thread.sleep(500);
    }


    @Body
    @POST
    public interface TestCallExecuteService {

        public Call<ReturnObject> callSuccess(ReturnObject returnObject);

        public Call<ReturnObject> callFail(ReturnObject returnObject);

    }

    @Body
    @POST
    public interface TestAsyncExecuteService {


        public ReturnObject asyncSuccess(ReturnObject returnObject);

        public ReturnObject asyncFail(ReturnObject returnObject);
    }

    public class TestExecuteServiceImpl implements TestAsyncExecuteService {

        @Override
        public ReturnObject asyncSuccess(ReturnObject returnObject) {
            ReturnObject newReturnObject = LightContext.lightContext().result();
            System.out.println(JSON.toJSONString(newReturnObject));
            Assert.assertEquals(newReturnObject.getValue(), "/asyncSuccess");
            return null;
        }

        @Override
        public ReturnObject asyncFail(ReturnObject returnObject) {
            System.out.println(LightContext.lightContext().throwable().getMessage());
            Assert.assertEquals(LightContext.lightContext().throwable().getMessage(), "404 Not Found");
            return null;
        }

    }
}
