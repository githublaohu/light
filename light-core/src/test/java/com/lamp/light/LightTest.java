package com.lamp.light;

import com.lamp.light.Light.Builder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.List;

public class LightTest {

	ReturnObject returnObject = new ReturnObject();

	//@Before
	public void init() {
		// 启动http服务
		Thread thread = new Thread(new Runnable() {
			@SuppressWarnings("restriction")
			@Override
			public void run() {
				HttpServer server;
				try {
					// 监听8080端口，同时受理 0个请求
					server = HttpServer.create(new InetSocketAddress(8000), 0);
					// 声明 path 和对应的处理逻辑
					server.createContext("/testInterface/testObject", exchange -> {
						// 写死处理http请求的逻辑
						InputStream requestBody = exchange.getRequestBody();
						byte[] bytes = new byte[requestBody.available()];
						requestBody.read(bytes);
						String str = new String(bytes);
						System.out.println(str);
					});
					server.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
		returnObject.setId(123);
		returnObject.setKey("key");
	}

	@Test
	public void test() throws Exception {
		// 构建light
		Builder builder = Light.Builder();
		Light light = builder.host("127.0.0.1").port(8080).build();
		TestInterface testInterface = light.create(TestInterface.class);
		ReturnObject newReturnObject = testInterface.testObject(returnObject);
		System.out.println(newReturnObject);
	}

	@Test
	public void testHead() throws Exception {
		Builder builder = Light.Builder();
		Light light = builder.host("127.0.0.1").port(8080).path("/light").build();
		TestInterface testInterface = light.create(TestInterface.class);
		testInterface.testHead(returnObject, "cccc");
	}

	@Test
	public void testData() throws Exception {
		Builder builder = Light.Builder();
		Light light = builder.host("127.0.0.1").port(8000).path("/light").build();
		TestInterface testInterface = light.create(TestInterface.class);
		testInterface.testData(returnObject);
	}

	@Test
	public void ttetet() {
		Retrofit retrofit = new Retrofit.Builder().baseUrl("http://127.0.0.1:8080").build();

		cedded cedded = retrofit.create(cedded.class);
		cedded.getsdde();
	}

	interface cedded {

		@GET("/dede")
		public Call<List<String>> getsdde();
	}
}
