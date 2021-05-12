package com.lamp.light;

import com.lamp.light.annotation.*;

@POST("/testInterface")
public interface TestInterface {

    @Headers({"Connet-Type:laohu"})
    @POST("/testHead}")
    ReturnObject testHead(@Header({"key", "id"}) ReturnObject returnObject, @Header("cccc") String cccc);

    @Headers({"Connet-Type:laohu"})
    @POST("/testCall}")
    Call<ReturnObject> testCall(@Header({"key", "id"}) ReturnObject returnObject, @Header("cccc") String cccc);

    @POST("/testHead}")
    ReturnObject testQuery(@Query({"key", "id"}) ReturnObject returnObject, @Query("query") String query);

    @POST("/testObject/{key}/{id}/{path}")
    ReturnObject testPath(@Path({"key", "id"}) ReturnObject returnObject, @Path("path") String path);

    @Body
    @POST("/testObject")
    ReturnObject testObject(ReturnObject returnObject);

    @Headers({"Connet-Type:laohu"})
    @POST("/testObject")
    ReturnObject testData(@Field({"key", "id"}) @Path({"key", "id"}) @Query({"key", "id"}) @Header({"key", "id"}) ReturnObject returnObject);


}
