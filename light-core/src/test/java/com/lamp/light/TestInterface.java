package com.lamp.light;

import com.lamp.light.annotation.*;

@POST("/testInterface")
public interface TestInterface {

    @Headers({"Connet-Type:laohu"})
    @POST("/testHead}")
    public ReturnObject testHead(@Header({"key","id"}) ReturnObject returnObject, @Header("cccc") String cccc);
    
    @Headers({"Connet-Type:laohu"})
    @POST("/testCall}")
    public Call<ReturnObject> testCall(@Header({"key","id"}) ReturnObject returnObject, @Header("cccc") String cccc);
    
    @POST("/testHead}")
    public ReturnObject testQuery(@Query({"key","id"}) ReturnObject returnObject, @Query("query") String query);
    
    @POST("/testObject/{key}/{id}/{path}")
    public ReturnObject testPath(@Path({"key","id"}) ReturnObject returnObject, @Path("path") String path);

    @Body
    @POST("/testObject")
    public ReturnObject testObject(ReturnObject returnObject);
    
    @Headers({"Connet-Type:laohu"})
    @POST("/testObject")
    public ReturnObject testData(@Field({"key","id"})@Path({"key","id"}) @Query({"key","id"}) @Header({"key","id"}) ReturnObject returnObject);
    
    
    
}
