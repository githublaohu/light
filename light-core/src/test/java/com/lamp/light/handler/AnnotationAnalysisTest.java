package com.lamp.light.handler;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.lamp.light.ReturnObject;
import com.lamp.light.TestInterface;
import com.lamp.light.api.request.RequestInfo;

public class AnnotationAnalysisTest {

    private Class<?> clazz = TestInterface.class;
    
    private AnnotationAnalysis annotationAnalysis = new AnnotationAnalysis();
    
    private RequestInfo requestInfo = new RequestInfo(); 
    
    
    @Before
    public void testClass() throws Exception {
        requestInfo = annotationAnalysis.analysis(clazz);
    }
    
    @Test
    public void testCall() throws Exception {
       Method method = clazz.getMethod("testCall", new Class[]{ReturnObject.class,String.class});
       annotationAnalysis.analysis(clazz,method,   requestInfo);
    }
    
    @Test
    public void testHead() throws Exception {
       Method method = clazz.getMethod("testHead", new Class[]{ReturnObject.class,String.class});
       annotationAnalysis.analysis(clazz,method,   requestInfo);
    }
    
    @Test
    public void testQuery() throws Exception {
       Method method = clazz.getMethod("testQuery", new Class[]{ReturnObject.class,String.class});
       annotationAnalysis.analysis(clazz,method,   requestInfo);
    }
    
    @Test
    public void testPath() throws Exception {
       Method method = clazz.getMethod("testPath", new Class[]{ReturnObject.class,String.class});
       annotationAnalysis.analysis(clazz,method,   requestInfo);
    }
    
    @Test
    public void testObject() throws Exception {
       Method method = clazz.getMethod("testObject", new Class[]{ReturnObject.class,String.class});
       annotationAnalysis.analysis(clazz,method,   requestInfo);
    }
}
