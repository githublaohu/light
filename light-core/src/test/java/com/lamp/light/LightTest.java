package com.lamp.light;

import org.junit.Assert;
import org.junit.Test;
import com.lamp.light.Light.Builder;


public class LightTest {

    ReturnObject returnObject = new ReturnObject("key", "value");
    TestInterface i;

    {
        Builder builder = Light.Builder();
        Light light = builder.host("127.0.0.1").port(8080).build();
        try {
            i = light.create(TestInterface.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBody() {
        ReturnObject res = i.testBody(returnObject);
        Assert.assertEquals(new ReturnObject("testBody", "value").toString(), res.toString());
    }

    @Test
    public void testField() {
        //配合表单测试
    }

    @Test
    public void testHeader() {
        ReturnObject res = i.testHeader(returnObject, "testHeader");
        Assert.assertEquals(new ReturnObject("key", "testHeader").toString(), res.toString());
    }

    @Test
    public void testHeaders() {
        Assert.assertEquals(new ReturnObject("createKey", "testHeaders").toString(),i.testHeaders().toString());
    }

    @Test
    public void testPath() {
        ReturnObject res = i.testPath(returnObject, "testPath");
        Assert.assertEquals(new ReturnObject("key", "testPath").toString(), res.toString());
    }

    @Test
    public void testQuery() {
        Assert.assertEquals(new ReturnObject("key", "testQuery2").toString(), i.testQuery(new ReturnObject("key", "testQuery"),"testQuery2").toString());
    }


    @Test
    public void testReqMethod() {
        Assert.assertEquals(new ReturnObject("createKey", "deleteTest").toString(), i.testDelete().toString());
        Assert.assertEquals(new ReturnObject("createKey", "getTest").toString(), i.testGet().toString());
        Assert.assertEquals(new ReturnObject("createKey", "headTest").toString(), i.testHead().toString());
        Assert.assertEquals(new ReturnObject("createKey", "patchTest").toString(), i.testPatch().toString());
        Assert.assertEquals(new ReturnObject("createKey", "postTest").toString(), i.testPost().toString());
        Assert.assertEquals(new ReturnObject("createKey", "putTest").toString(), i.testPut().toString());
    }
}
