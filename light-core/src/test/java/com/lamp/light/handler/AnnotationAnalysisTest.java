package com.lamp.light.handler;

import java.lang.reflect.Method;

import org.junit.Test;

import com.lamp.light.ReturnObject;
import com.lamp.light.TestInterface;

public class AnnotationAnalysisTest {

    private Class<?> clazz = TestInterface.class;
    
    private AnnotationAnalysis annotationAnalysis = new AnnotationAnalysis();
    
    private RequestInfo requestInfo = new RequestInfo(); 
    
    @Test
    public void testHead() throws Exception {
       Method method = clazz.getMethod("testHead", new Class[]{ReturnObject.class,String.class});
       annotationAnalysis.analysis(method,   requestInfo);
    }
}
