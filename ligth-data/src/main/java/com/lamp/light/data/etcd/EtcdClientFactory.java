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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import io.etcd.jetcd.Auth;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.Cluster;
import io.etcd.jetcd.Election;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Lock;
import io.etcd.jetcd.Maintenance;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.LeaseOption;
import io.grpc.stub.StreamObserver;

import com.lamp.electron.register.api.AbstractRegisterModel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EtcdClientFactory {

	private Map<String, EtcdLeaseId> etcdLeaseIdMap = new ConcurrentHashMap<>();

	public Client createClient(String url, AbstractRegisterModel abstractRegisterModel) {
		EtcdLeaseId etcdLeaseId = etcdLeaseIdMap.get(url);
		if (Objects.nonNull(etcdLeaseId)) {
			etcdLeaseId.getAbstractRegisterModel().add(abstractRegisterModel);
			return etcdLeaseId.getClientWrapper();
		}
		String address = url;
		ClientBuilder clientBuilder = Client.builder();
		String[] addresss = address.split(",");
		String[] httpAddress = new String[addresss.length];
		for (int i = 0; i < addresss.length; i++) {
			httpAddress[i] = "http://" + addresss[i];
		}
		etcdLeaseId = new EtcdLeaseId();
		try {
			etcdLeaseId.setUrl(url);
			etcdLeaseId.getAbstractRegisterModel().add(abstractRegisterModel);
			etcdLeaseId.setClientBuilder(clientBuilder.endpoints(httpAddress));
			etcdLeaseId.setClientWrapper(new ClientWrapper(etcdLeaseId.getClientBuilder().build()));
			ClientWrapper client = etcdLeaseId.getClientWrapper();
			long leaseId = client.getLeaseClient().grant(10L).get().getID();
			etcdLeaseId.setLeaseId(leaseId);
			EtcdStreamObserver etcdStreamObserver = new EtcdStreamObserver();
			etcdStreamObserver.etcdLeaseId = etcdLeaseId;
			etcdLeaseId.setEtcdStreamObserver(etcdStreamObserver);
			client.getLeaseClient().keepAlive(leaseId, etcdStreamObserver);

			etcdLeaseIdMap.put(url, etcdLeaseId);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		return etcdLeaseId.getClientWrapper();
	}

	public Long getLeaseId(String url) {
		return etcdLeaseIdMap.get(url).getLeaseId();
	}

	private void createLeaseId(EtcdLeaseId etcdLeaseId) {
		log.warn("renewal failed,renewal of contract. server url {}", etcdLeaseId.getUrl());
		Client client = etcdLeaseId.getClientWrapper();
		try {
			long ttl = client.getLeaseClient().timeToLive(etcdLeaseId.getLeaseId(), LeaseOption.DEFAULT).get().getTTl();
			if (ttl < 1) {
				long leaseId = client.getLeaseClient().grant(1L).get().getID();
				client.getLeaseClient().keepAlive(leaseId, etcdLeaseId.getEtcdStreamObserver());
				etcdLeaseId.setLeaseId(leaseId);
				for (AbstractRegisterModel abstractRegisterModel : etcdLeaseId.abstractRegisterModel) {
					abstractRegisterModel.deregister();
				}
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			client.getLeaseClient().keepAlive(System.currentTimeMillis(), etcdLeaseId.getEtcdStreamObserver());
		}
	}

	@Data
	class EtcdLeaseId {

		private long leaseId;

		private AtomicBoolean renewalLock = new AtomicBoolean();

		private ClientWrapper clientWrapper;

		private ClientBuilder clientBuilder;

		private String url;

		private EtcdStreamObserver etcdStreamObserver;

		private List<AbstractRegisterModel> abstractRegisterModel = new CopyOnWriteArrayList<>();

	}

	class ClientWrapper implements Client {

		private volatile Client client;

		public ClientWrapper(Client client) {
			this.client = client;
		}

		@Override
		public Auth getAuthClient() {
			return client.getAuthClient();
		}

		@Override
		public KV getKVClient() {
			return client.getKVClient();
		}

		@Override
		public Cluster getClusterClient() {
			return client.getClusterClient();
		}

		@Override
		public Maintenance getMaintenanceClient() {
			return client.getMaintenanceClient();
		}

		@Override
		public Lease getLeaseClient() {
			return client.getLeaseClient();
		}

		@Override
		public Watch getWatchClient() {
			return client.getWatchClient();
		}

		@Override
		public Lock getLockClient() {
			return client.getLockClient();
		}

		@Override
		public Election getElectionClient() {
			return client.getElectionClient();
		}

		@Override
		public void close() {
			client.close();
		}

	}

	class EtcdStreamObserver implements StreamObserver<LeaseKeepAliveResponse> {

		private EtcdLeaseId etcdLeaseId;

		@Override
		public void onNext(LeaseKeepAliveResponse value) {
		}

		@Override
		public void onError(Throwable t) {
			log.error("EtcdStreamObserver " + t.getMessage(), t);
			EtcdClientFactory.this.createLeaseId(etcdLeaseId);
		}

		@Override
		public void onCompleted() {
			EtcdClientFactory.this.createLeaseId(etcdLeaseId);
		}

	}
}
