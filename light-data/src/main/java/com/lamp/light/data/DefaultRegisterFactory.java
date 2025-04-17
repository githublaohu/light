/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PubL v2.
 *You can use this software according to the terms and conditions of the Mulan PubL v2.
 *You may obtain a copy of Mulan PubL v2 at:
 *         http://license.coscl.org.cn/MulanPubL-2.0
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PubL v2 for more details.
 */
package com.lamp.light.data;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.lamp.electron.register.consul.ConsulRegisterObjectFactory;
import com.lamp.electron.register.etcd.EtcdRegisterObjectFactory;
import com.lamp.electron.register.eureka.EurekaRegisterObjectFactory;
import com.lamp.electron.register.nacos.NacosRegisterObjectFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认注册工厂，支持consul、etcd、eureka
 */
@Slf4j
public class DefaultRegisterFactory implements RegisterFactory{

	private static final Map<String, RegisterObjectFactory> registerObjectFactoryMap = new HashMap<>();

	static {
		ConsulRegisterObjectFactory consulRegisterObjectFactory = new ConsulRegisterObjectFactory();
		registerObjectFactoryMap.put(consulRegisterObjectFactory.registerCenterName(), consulRegisterObjectFactory);

		EtcdRegisterObjectFactory etcdRegisterObjectFactory = new EtcdRegisterObjectFactory();
		registerObjectFactoryMap.put(etcdRegisterObjectFactory.registerCenterName(), etcdRegisterObjectFactory);

		registerObjectFactoryMap.put(etcdRegisterObjectFactory.registerCenterName(), etcdRegisterObjectFactory);
//		registerObjectFactoryMap.put("", etcdRegisterObjectFactory);

		NacosRegisterObjectFactory nacosRegisterObjectFactory = new NacosRegisterObjectFactory();
		registerObjectFactoryMap.put(nacosRegisterObjectFactory.registerCenterName(), nacosRegisterObjectFactory);

		EurekaRegisterObjectFactory eurekaRegisterObjectFactory = new EurekaRegisterObjectFactory();
		registerObjectFactoryMap.put(eurekaRegisterObjectFactory.registerCenterName(), eurekaRegisterObjectFactory);
}

	/**
	 * 扩展类
	 */
	private Map<Class<?>, CurrencyRegisterServer> registerServerAndCurrencyRegisterServer = new ConcurrentHashMap<>();

	private Map<Class<?>, Object> proxyObject = new ConcurrentHashMap<>();

	private String register;

	private String prefix;

	public DefaultRegisterFactory(String register, PrefixFactory prefixFactory) {
		this(register, prefixFactory, null);
	}

	public DefaultRegisterFactory(String register, PrefixFactory prefixFactory, String prefix) {
		this.register = register;
		this.prefix = prefixFactory.createPrefix(prefix);
		log.info("register {} start , root path is {}" , this.register, this.prefix);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T createRegisterObject(Class<?> clazz) {
		try {
			RegisterData registerData = getRegisterData(clazz, prefix);
			if (Objects.isNull(registerData)) {
				return null;
			}
			Object proxy = proxyObject.get(clazz);
			CurrencyRegisterServer currencyRegisterServer;
			if (Objects.isNull(proxy)) {
				currencyRegisterServer = new CurrencyRegisterServer();
				proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { clazz },
						currencyRegisterServer);
				registerServerAndCurrencyRegisterServer.put(clazz, currencyRegisterServer);
				proxyObject.put(clazz, proxy);
			}else{
				currencyRegisterServer = registerServerAndCurrencyRegisterServer.get(clazz);
			}
			currencyRegisterServer.addRegisterServer(getNoticeModelList(this.register, registerData, null));
			return (T) proxy;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void createMonitorObjectTo(List<RegisterServer<?>> registerServerList) throws Exception {
		List<RegisterServer<Object>> list = new ArrayList<>();
		registerServerList.forEach(t -> list.add( (RegisterServer<Object>)t) );
		createMonitorObject(list, this.register, this.prefix);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void createMonitorObject(RegisterServer<?> registerServerList) throws Exception {
		List<RegisterServer<Object>> list = new ArrayList<>();
		list.add((RegisterServer<Object>) registerServerList);
		createMonitorObject(list, this.register, this.prefix);
	}

	@Override
	public void createMonitorObject(List<RegisterServer<Object>> registerServerList, String url, String prefix)
			throws Exception {
		ConcurrentHashMap<Class<?>, CurrencyRegisterServer> clazzMap = new ConcurrentHashMap<>();
		for (RegisterServer<Object> registerServer : registerServerList) {
			Class<?>[] interfaces = registerServer.getClass().getInterfaces();
			boolean isRegister = false;
			for (Class<?> clazz : interfaces) {
				RegisterData registerData = getRegisterData(clazz, prefix);
				if (Objects.isNull(registerData)) {
					continue;
				}
				isRegister = true;
				CurrencyRegisterServer currencyRegisterServer = clazzMap.computeIfAbsent(clazz, new Function<Class<?>, CurrencyRegisterServer>() {
					@Override
					public CurrencyRegisterServer apply(Class<?> t) {

						CurrencyRegisterServer currencyRegisterServer = new CurrencyRegisterServer();
						try {
							getNoticeModelList(url, registerData, currencyRegisterServer);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						return currencyRegisterServer;
					}
				});
				// 数据注册为空，可以不管他
				currencyRegisterServer.addRegisterServer(registerServer);
			}
			if (!isRegister) {
				throw new RuntimeException("必须");

			}
		}
	}

	private List<RegisterServer<Object>> getNoticeModelList(String url, RegisterData registerData,
			RegisterServer<Object> object) throws Exception {
		String[] urlArray = StringUtils.split(url, ";");
		List<RegisterServer<Object>> noticeModelList = new ArrayList<>();
		for (String urlString : urlArray) {
			RegisterData newRegisterData = RegisterData.cloneObject(registerData);
			newRegisterData.setServerUrl(urlString);
			RegisterModel<Object> noticeModel = getRegisterModel(object, newRegisterData);
			if (Objects.nonNull(noticeModel)) {
				noticeModelList.add(noticeModel);
			}
		}
		return noticeModelList;
	}

	private RegisterModel<Object> getRegisterModel(RegisterServer<Object> registerServer, RegisterData registerData)
			throws Exception {
		String url = registerData.getServerUrl();
		int index = url.indexOf("://");
		String registerName = "";
		if (index != -1) {
			registerName = url.substring(0, index);
			registerData.setServerUrl(url.substring(index + 3));
		}
		RegisterObjectFactory registerObjectFactory = registerObjectFactoryMap.get(registerName);
		if(!registerObjectFactory.electronRegister()) {
			// 兼容的注册中心，只支持实例的注册
			if (!StringUtils.equals(registerData.getDataClass().getSimpleName(), "ExampleInfo")) {
				return null;
			}
		}
		return registerObjectFactory.createRegisterModel(registerServer, registerData);
	}

	private RegisterData getRegisterData(Class<?> clazz, String prefix) {
		Register register = clazz.getAnnotation(Register.class);
		if (Objects.isNull(register) || Objects.isNull(register.node())) {
			return null;
		}
		RegisterData registerData = new RegisterData();
		registerData.setNodeName(register.node());
		registerData.setPersistence(register.persistence());
		Type[] types = clazz.getGenericInterfaces();
		ParameterizedType parameterizedType = (ParameterizedType) types[0];
		Type type = parameterizedType.getActualTypeArguments()[0];
		registerData.setDataClass((Class<?>) type);
		registerData.setPath(prefix + "/" + registerData.getDataClass().getSimpleName());
		return registerData;
	}

	public static class CurrencyRegisterServer implements RegisterServer<Object>, InvocationHandler {

		private List<RegisterServer<Object>> registerModelList = new ArrayList<>();

		public void addRegisterServer(List<RegisterServer<Object>> registerServerList) {
			registerModelList.addAll(registerServerList);
		}
		
		public void addRegisterServer(RegisterServer<Object> registerServer) {
			registerModelList.add(registerServer);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (Objects.isNull(args) || Objects.isNull(args[0])) {
				return -1;
			}
			try {
				String name = method.getName();
				Object object = args[0];
				for (RegisterServer<Object> registerModel : registerModelList) {
					if ("register".equals(name)) {
						registerModel.register(object);
					}
					if ("batchRegister".equals(name)) {
						List<Object> objectList = (List<Object>) object;
						for (Object o : objectList) {
							registerModel.register(o);
						}
					}
					if ("deregister".equals(name)) {
						registerModel.deregister(object);
					}
				}
			} catch (Exception e) {

			}
			return 1;
		}

		@Override
		public int register(Object t) {
			for (RegisterServer<Object> registerModel : registerModelList) {
				try {
					registerModel.register(t);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			return 0;
		}

		@Override
		public int deregister(Object t) {
			for (RegisterServer<Object> registerModel : registerModelList) {
				registerModel.deregister(t);
			}
			return 0;
		}

	}
}
