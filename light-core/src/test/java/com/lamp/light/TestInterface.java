package com.lamp.light;

import com.lamp.light.annotation.reqmethod.*;
import com.lamp.light.annotation.reqparam.*;

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



    //reqMethod
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
