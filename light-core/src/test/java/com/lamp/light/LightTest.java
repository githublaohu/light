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
import java.net.InetSocketAddress;
import java.util.List;

public class LightTest {
    
    ReturnObject returnObject = new ReturnObject();
    
    @Before
    public void init() {

        
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                HttpServer server;
                try {
                    server = HttpServer.create(new InetSocketAddress(8000), 0);
                    server.createContext("/testInterface/testObject", new HttpHandler() {
                        
                        @Override
                        public void handle(HttpExchange exchange) throws IOException {
                            String response = "hello world";

                                //获得查询字符串(get)
                              /*  String queryString =  exchange.getRequestURI().getQuery();
                                Map<String,String> queryStringInfo = formData2Dic(queryString);
                                //获得表单提交数据(post)
                                String postString = IOUtils.toString(exchange.getRequestBody());
                                Map<String,String> postInfo = formData2Dic(postString);

                                exchange.sendResponseHeaders(200,0);
                                OutputStream os = exchange.getResponseBody();
                                os.write(response.getBytes());
                                os.close();*/
                            
                        }
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
        Builder builder = Light.Builder();
        Light light = builder.host("192.168.2.199").port(8844).build();

        TestInterface testInterface = light.create(TestInterface.class);
        testInterface.testObject(returnObject);

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
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8080")
            .build();
        
        cedded cedded = retrofit.create(cedded.class);
        cedded.getsdde();
    }
    
    interface cedded{
        
        @GET("/dede")
        public Call<List<String>> getsdde();
    }
}
