package com.lamp.light.handler;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import io.netty.channel.Channel;

public class Http11Factory {
	
	
	private static final Http11Factory INSTANCE = new Http11Factory();
	

    public static Http11Factory getInstance() {
        return INSTANCE;
    }
	
	private Http11Factory() {}
	
	
	
	private Map<InetSocketAddress, LinkedBlockingQueue<ChannelWrapper>> channelWrapperCache = new ConcurrentHashMap<>();
	
	private Map<InetSocketAddress, LinkedBlockingQueue<ChannelWrapper>> securityChannelWrapperCache = new ConcurrentHashMap<>();

	
	private Map<InetSocketAddress, LinkedBlockingQueue<ChannelWrapper>> getCache(boolean isSecurity){
		
		return isSecurity ? securityChannelWrapperCache:channelWrapperCache;
	}
	
	public ChannelWrapper getChannelWrapper(boolean isSecurity,InetSocketAddress address) {
		Map<InetSocketAddress, LinkedBlockingQueue<ChannelWrapper>> cache = this.getCache(isSecurity);
		LinkedBlockingQueue<ChannelWrapper> queue = cache.get(address);
		if (Objects.isNull(queue)) {
			queue = cache.computeIfAbsent(address, key -> new LinkedBlockingQueue<>());
			return null;
		} else {
			long time = System.currentTimeMillis();
			for (;;) {
				ChannelWrapper channelWrapper = queue.poll();
				if (Objects.isNull(channelWrapper)) {
					return null;
				}
				if (channelWrapper.timeout > time && channelWrapper.max > 0) {
					channelWrapper.max--;
					return channelWrapper;
				}
			}

		}
	}

	public void setChannelWrapper(ChannelWrapper channelWrapper) {
		long time = System.currentTimeMillis();
		if (channelWrapper.timeout < time && channelWrapper.max <= 0) {
			return;
		}
		Map<InetSocketAddress, LinkedBlockingQueue<ChannelWrapper>> cache = this.getCache(channelWrapper.isSecurity);
		LinkedBlockingQueue<ChannelWrapper> queue = cache.get(channelWrapper.address);
		if (Objects.isNull(queue)) {
			queue = cache.computeIfAbsent(channelWrapper.address, key -> new LinkedBlockingQueue<>());
		}
		queue.add(channelWrapper);
	}

	public static class ChannelWrapper {

		InetSocketAddress address;
		
		boolean isSecurity = false;

		Channel channel;

		long timeout;

		int max = 100;

		public InetSocketAddress getAddress() {
			return address;
		}

		public void setAddress(InetSocketAddress address) {
			this.address = address;
		}

		public boolean isSecurity() {
			return isSecurity;
		}

		public void setSecurity(boolean isSecurity) {
			this.isSecurity = isSecurity;
		}

		public Channel getChannel() {
			return channel;
		}

		public void setChannel(Channel channel) {
			this.channel = channel;
		}

		public long getTimeout() {
			return timeout;
		}

		public void setTimeout(long timeout) {
			this.timeout = timeout;
		}

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}
		
		

	}

}
