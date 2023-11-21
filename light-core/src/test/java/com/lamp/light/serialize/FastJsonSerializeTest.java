package com.lamp.light.serialize;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FastJsonSerializeTest {


    FastJsonSerialize fastJsonSerialize = new FastJsonSerialize();

    @SuppressWarnings("unchecked")
    @Test
    public void testCollection() throws NoSuchMethodException, SecurityException {
        Method method = this.getClass().getMethod("getTest");
        Class<?> clazz = method.getReturnType();

        Map<String, Testsss> map = new HashMap<>();
        Testsss testsss = new Testsss();
        testsss.setStes("123123");
        map.put("123", testsss);
        byte[] data = fastJsonSerialize.serialize(map);
        Map<String, Testsss> newMap = (Map<String, Testsss>) fastJsonSerialize.deserialization(clazz, data);
        newMap.toString();
    }


    public Map<String, Testsss> getTest() {
        return null;
    }

    static class Testsss {

        private String stes;

        public String getStes() {
            return stes;
        }

        public void setStes(String stes) {
            this.stes = stes;
        }


    }
}
