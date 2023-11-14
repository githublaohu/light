package com.lamp.light.cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 厂商API模式：<br>
 * <blockquote><br>
 * 1. 如果不指定就代理模式 <br>
 * 2. 生成指定对象 <br>
 * 3. 如果只有唯一配置，就生成默认对象 <br>
 * 简单点，使用代理 </blockquote><br>
 * 普通模式： <br>
 * <blockquote><br>
 * 识别注解使用原生<br>
 * 1. 直接 <br>
 * </blockquote><br>
 * 功能: <br>
 * <blockquote><br>
 * 1. 日志 <br>
 * 2. 路由 <br>
 * 3. 链路 <br>
 * 4. 时间监控 <br>
 * 5. 注册成功与失败处理接口 </blockquote><br>
 *
 * @author laohu
 */
public class ConfigService {

    private static final String CIRUIT_CRITERION = "META-INF/ligth.criterion";

    private static final String CIRUIT_REALIZATION = "META-INF/ligth.realization";

    private static final String CIRUIT_INTERCEPTOR = "META-INF/ligth.interceptor";

    private ClassLoader classLoader = getClassLoader(ConfigService.class);

    private Map<Class<?>, CircuitInvocationHandler> circuitInvocationHandlerMap = new ConcurrentHashMap<>();

    private Map<Class<?>, Object> objectMap = new ConcurrentHashMap<>();

    private Map<Class<?>, Map<String, Class<?>>> circuitAndRealizationMap = new ConcurrentHashMap<>();

    private Map<ManufacturerInfo, Class<?>> interceptorMap = new ConcurrentHashMap<>();

    private Map<Class<?>, Object> mapperMap = new ConcurrentHashMap<>();

    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class
            // loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = clazz.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with
                    // null...
                }
            }
        }
        return cl;
    }

    public ConfigService() {
        this.readCriterion();
        this.readRealization();
        this.readInterceptor();
    }

    private void readCriterion() {
        try {
            Enumeration<URL> urls = classLoader.getResources(CIRUIT_CRITERION);
            while (urls.hasMoreElements()) {
                List<Class<?>> list = loadResource(classLoader, urls.nextElement());
                for (Class<?> clazz : list) {
                    CircuitInvocationHandler handleProxy = new CircuitInvocationHandler();
                    Object proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handleProxy);
                    circuitInvocationHandlerMap.put(clazz, handleProxy);
                    objectMap.put(clazz, proxy);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    private void readRealization() {
        try {
            Enumeration<URL> urls = classLoader.getResources(CIRUIT_REALIZATION);
            while (urls.hasMoreElements()) {
                List<Class<?>> list = loadResource(classLoader, urls.nextElement());
                for (Class<?> clazz : list) {
                    for (Class<?> interfaces : clazz.getInterfaces()) {
                        CircuitInvocationHandler circuitInvocationHandler = circuitInvocationHandlerMap.get(interfaces);
                        if (Objects.nonNull(circuitInvocationHandler)) {

                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void readInterceptor() {
        try {
            Enumeration<URL> urls = classLoader.getResources(CIRUIT_INTERCEPTOR);
            while (urls.hasMoreElements()) {
                List<Class<?>> list = loadResource(classLoader, urls.nextElement());
                for (Class<?> clazz : list) {
                    for (Class<?> interfaces : clazz.getInterfaces()) {
                        CircuitInvocationHandler circuitInvocationHandler = circuitInvocationHandlerMap.get(interfaces);
                        if (Objects.nonNull(circuitInvocationHandler)) {

                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<Class<?>> loadResource(ClassLoader classLoader, java.net.URL resourceURL) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
            String line;
            List<Class<?>> criterionClass = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                try {
                    criterionClass.add(Class.forName(line));
                } catch (Throwable t) {
                    throw new IllegalStateException("Failed to load extension class ( class line: " + line + ") in "
                            + resourceURL + ", cause: " + t.getMessage(), t);
                }
            }
            return null;
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }

    }
}
