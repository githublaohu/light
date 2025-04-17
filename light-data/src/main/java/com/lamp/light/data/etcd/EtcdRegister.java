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
package com.lamp.light.data.etcd;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch.Listener;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchEvent.EventType;
import io.etcd.jetcd.watch.WatchResponse;

import com.alibaba.fastjson.JSON;
import com.lamp.electron.register.api.AbstractRegisterModel;
import com.lamp.electron.register.api.RegisterData;
import com.lamp.electron.register.api.RegisterServer;

/**
 * Etcd注册模型
 * @author jellly
 */
public class EtcdRegister extends AbstractRegisterModel {

	private static final Logger log = LoggerFactory.getLogger(EtcdRegister.class);

	private Map<ByteSequence, ByteSequence> reRegisterData = new ConcurrentHashMap<>();

	private EtcdClientFactory etcdClientFactory;

	private WatcherListen watcherListen;

	private Client client;

	private Long leaseId;

	public EtcdRegister(EtcdClientFactory etcdClientFactory, RegisterServer<Object> registerServers,
			RegisterData registerData) {
		super(registerServers, registerData);
		this.etcdClientFactory = etcdClientFactory;
		this.client = etcdClientFactory.createClient(registerData.getServerUrl(), this);
		this.leaseId = etcdClientFactory.getLeaseId(registerData.getServerUrl());
		if (Objects.nonNull(registerServers)) {
			try {
				watcherListen = new WatcherListen(ByteSequence.from(registerData.getPath().getBytes()));
				getData();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private void getData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					log.info("拉取path[{}]所有数据",registerData.getPath());
					GetOption getOption = GetOption.newBuilder()
							.withPrefix(ByteSequence.from(registerData.getPath().getBytes())).build();
					List<KeyValue> keyValues = client.getKVClient()
							.get(ByteSequence.from(registerData.getPath().getBytes()), getOption).get().getKvs();
					for (KeyValue kv : keyValues) {
						Object object = JSON.parseObject(kv.getValue().getBytes(), registerData.getDataClass());
						registerServers.register(object);
					}
					// 拉去所有数据在watcher，防止watcher新数据被path旧数据覆盖
					client.getWatchClient().watch(watcherListen.byteSequence,WatchOption.newBuilder().withPrefix(watcherListen.byteSequence).build(), watcherListen);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}

			}
		}).start();
	}

	@Override
	public void deregister() throws Exception {
		log.warn("deregister, registerData is {}", registerData);
		this.leaseId = etcdClientFactory.getLeaseId(registerData.getServerUrl());
		if (Objects.nonNull(registerServers)) {
			getData();
		} else {
			for (Entry<ByteSequence, ByteSequence> entry : reRegisterData.entrySet()) {
				client.getKVClient()
						.put(entry.getKey(), entry.getValue(), PutOption.newBuilder().withLeaseId(leaseId).build())
						.get().getHeader().getRevision();
			}
		}
	}

	@Override
	public int register(Object data) {
		try {
			ByteSequence key = ByteSequence.from(getKey(data).getBytes());
			ByteSequence value = ByteSequence.from(JSON.toJSONString(data).getBytes());
			PutOption putOption = registerData.isPersistence() ? PutOption.DEFAULT
					: PutOption.newBuilder().withLeaseId(leaseId).build();
			if (!registerData.isPersistence()) {
				reRegisterData.put(key, value);
			}
			return (int) client.getKVClient().put(key, value, putOption).get().getHeader().getRevision();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int deregister(Object data) {

		try {
			ByteSequence key = ByteSequence.from(getKey(data).getBytes());
			client.getKVClient().delete(key).get().getHeader().getRaftTerm();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	private class WatcherListen implements Listener {

		ByteSequence byteSequence;

		/**
		 * 防止重复watch
		 */
		AtomicBoolean uniqueWatch = new AtomicBoolean(false);

		public WatcherListen(ByteSequence byteSequence) {
			this.byteSequence = byteSequence;
		}

		public void watcher() {
			client.getWatchClient().watch(byteSequence, this);
			uniqueWatch.compareAndSet(false, true);
		}

		@Override
		public void onNext(WatchResponse response) {
			uniqueWatch.compareAndSet(true, false);
			for (WatchEvent watchEvent : response.getEvents()) {
				if (Objects.equals(watchEvent.getEventType(), EventType.PUT)) {
					registerServers.register(watchEvent.getKeyValue().getValue());
				} else if (Objects.equals(watchEvent.getEventType(), EventType.DELETE)) {
					registerServers.deregister(watchEvent.getPrevKV().getValue());
				}
			}
			try {
				this.watcher();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}

		@Override
		public void onError(Throwable throwable) {
			log.error(throwable.getMessage(), throwable);
		}

		@Override
		public void onCompleted() {
			log.info("onCompleted");
		}

	}

}