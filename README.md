## 说明
 > light基于netty实现的易用轻量级异步http client库，支持spring与spring boot注入。
 
## 依赖
 
### 直接使用
```xml
<dependency>
	<groupId>cn.lampup</groupId>
	<artifactId>light-core</artifactId>
	<version>${version}</version>
</dependency>
```
 
### API包使用
```xml
<dependency>
	<groupId>cn.lampup</groupId>
	<artifactId>light-api</artifactId>
	<version>${version}</version>
</dependency>
```
## 使用
 
### 方法与参数

```java
@POST("/testInterface")
public interface TestInterface {

    @Body
    @POST("/testBody")
    ReturnObject testBody(ReturnObject returnObject);

    @POST("/testHeader")
    ReturnObject testHeader(@Header({"key", "value"}) ReturnObject returnObject, @Header("testStr") String testStr);

    @Headers({"testHeader:testHeaders"})
    @POST("/testHeaders")
    ReturnObject testHeaders();

    @POST("/testPath/{key}/{value}/{path}")
    ReturnObject testPath(@Path({"key","value"}) ReturnObject returnObject,@Path("path") String path);

    @GET("/testQuery")
    ReturnObject testQuery(@Query({"key","value"}) ReturnObject returnObject,@Query("path") String path);


    @DELETE("/deleteTest")
    ReturnObject testDelete();

    @GET("/getTest")
    ReturnObject testGet();

    @GET("/headTest")
    ReturnObject testHead();

    @PATCH("/patchTest")
    ReturnObject testPatch();

    @POST("/postTest")
    ReturnObject testPost();

    @PUT("/putTest")
    ReturnObject testPut();

    @Headers({"Connet-Type:laohu"})
    @POST("/testObject")
    ReturnObject testData(@Field({"key", "value"}) @Path({"key", "value"}) @Query({"key", "value"}) @Header({"key", "value"}) ReturnObject returnObject);
}
```
 
### 上传文件
可以使用MultipartUpload对象与Multipart注解用于上传文件


#### 案例
```java
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
		File newFile = new File(file.getAbsolutePath()+"test.txt");
		try {
			newFile.delete();
			try(FileWriter fileWriter = new FileWriter(newFile)){
				for(int i = 0 ; i < 10 ; i++) {
				fileWriter.append(UUID.randomUUID().toString());
				}
			}
			
			multipartUploadTest.testFileMultipart(newFile);
			
			MultipartUpload multipartUpload = MultipartUpload.Builder().updateFile(newFile).build();
			multipartUploadTest.testFileMultipartUpload(multipartUpload);
			
			
		}finally {
			newFile.delete();
		}
		
		
	}
	
	@POST
	interface MultipartUploadTest{
		
		public ReturnObject testFileMultipartUpload(MultipartUpload multipartUpload );
		
		public ReturnObject testStringMultipartUpload(MultipartUpload multipartUpload);
		
		public ReturnObject testInputStreamMultipartUpload(MultipartUpload multipartUpload);
		
		public ReturnObject testBytesMultipartUpload(MultipartUpload multipartUpload);
		
		public ReturnObject testStringMultipart(@Multipart String string);
		
		public ReturnObject testFileMultipart(@Multipart File string);
		
}
```

#### Multipart
```java
public @interface Multipart {

	String value() default "";
	
	String name() default "";
	
	String format() default "";
}
```

#### MultipartUpload
```java

public class MultipartUpload {
	
	private String name;

	private String fileName;
	
	private String contentType;
	
	private Charset charset;
	
	private long size;
	
	private InputStream uploadStream;
	
	private File updateFile;
}
```


### 下载文件
 
### call与异步
```java
package com.lamp.light;

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
	            if(exchange.getRequestURI().getPath().endsWith("Fail")) {
	            	exchange.sendResponseHeaders(404, 0);
	            }else {
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
        testExecuteService = light.create(TestCallExecuteService.class );
        testAsyncExecuteService = light.create(TestAsyncExecuteService.class , new TestExecuteServiceImpl());
	}
	
	
	@Test
	public void testCall() throws InterruptedException{
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
	public interface TestCallExecuteService{		
		
		public Call<ReturnObject> callSuccess(ReturnObject returnObject);
		
		public Call<ReturnObject> callFail(ReturnObject returnObject);
		
	}
	
	@Body
	@POST
	public interface TestAsyncExecuteService{
		
		
		public ReturnObject asyncSuccess(ReturnObject returnObject);
		
		public ReturnObject asyncFail(ReturnObject returnObject);
	}
	
	public class TestExecuteServiceImpl implements TestAsyncExecuteService{

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
```
 
### http协议

### 拦截器

```java
public interface Interceptor {

    public default Object[] handlerBefore(Object proxy, Method method , RequestInfo requestInfo , Object[] args) {
    	return args;
    }
    
    public default HttpRequest handlerRequest( RequestInfo requestInfo,HttpRequest defaultFullHttpRequest) {
    	return defaultFullHttpRequest;
    }
    
    public default void handlerResponse(HttpResponse defaultHttpResponse) { }
    
    public default void handlerAfter(RequestInfo requestInfo,HttpResponse defaultHttpResponse) {
    	
    }
}
```
调用循序是 handlerBefore->handlerRequest->handlerResponse->handlerAfter
 
### 序列化
需要实现Serialize接口。默认序列化化方式是FastJsonSerialize
 
```java
 public interface Serialize {

    public byte[] serialize(Object object);
    
    public <T> T deserialization(Type t, byte[] data);
}
```

#### 直接使用
在创建Ligth对象的时候传入就好。
```java
Light light = builder.host("127.0.0.1").port(8001).serialize(new FastJsonSerialize()).build();
```

#### body指定序列化方式

```java
public @interface Body {
    Class<?> serialize() default Serialize.class;
}
@Body(serialize =FastJsonSerialize.class )
```

### 路由与广播
想动态指定服务端网络地址或则使用注册中心的时候可以使用路由模式，实现RouteSelect接口

#### 路由
路由接口
```java
public interface RouteSelect {
	public LampInstance select(Object[] args, Class<?> clazz);
}
Light light = builder.host("127.0.0.1").port(8001).routeSelect(new MyRouteSelect()).build();
```
 
#### 广播
有一些场景需要广播模式，可以实现BroadcastRouteSelect对象
```java
public interface BroadcastRouteSelect extends RouteSelect {

	public List<LampInstance> selects(Object[] args, Class<?> clazz);
}
```
 
 
## spring 使用
 

