package com.lamp.light;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.lamp.light.Light.Builder;
import com.lamp.light.api.http.annotation.method.POST;
import com.lamp.light.api.http.annotation.parameter.Multipart;
import com.lamp.light.api.multipart.MultipartUpload;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class MultipartTest {

    ReturnObject returnObject = new ReturnObject("key", "value");

    MultipartUploadTest multipartUploadTest;

    String userDis = System.getProperty("user.dir");
    File file = new File(userDis + "/lamp/");

    @Before
    public void init() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/", new HttpHandler() {

            @Override
            public void handle(HttpExchange exchange) throws IOException {
                OutputStream os = exchange.getResponseBody();

                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseHeaders().set("Content", "application/json");

                ReturnObject returnObject = new ReturnObject("key", exchange.getRequestURI().toString());
                os.write(JSON.toJSONBytes(returnObject));

                os.close();
            }

        });
        server.start();

        Builder builder = Light.Builder();
        Light light = builder.host("127.0.0.1").port(8001).build();
        multipartUploadTest = light.create(MultipartUploadTest.class);
    }

    @Test
    public void testFile() throws IOException {
        File newFile = new File(file.getAbsolutePath() + "test.txt");
        try {
            newFile.delete();
            try (FileWriter fileWriter = new FileWriter(newFile)) {
                for (int i = 0; i < 10; i++) {
                    fileWriter.append(UUID.randomUUID().toString());
                }
            }

            multipartUploadTest.testFileMultipart(newFile);

            MultipartUpload multipartUpload = MultipartUpload.Builder().updateFile(newFile).build();
            multipartUploadTest.testFileMultipartUpload(multipartUpload);


        } finally {
            newFile.delete();
        }


    }

    @POST
    interface MultipartUploadTest {

        public ReturnObject testFileMultipartUpload(MultipartUpload multipartUpload);

        public ReturnObject testStringMultipartUpload(MultipartUpload multipartUpload);

        public ReturnObject testInputStreamMultipartUpload(MultipartUpload multipartUpload);

        public ReturnObject testBytesMultipartUpload(MultipartUpload multipartUpload);

        public ReturnObject testStringMultipart(@Multipart String string);

        public ReturnObject testFileMultipart(@Multipart File string);

    }
}
