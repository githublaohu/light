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
package com.lamp.light.netty;

import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLException;

import com.lamp.light.LightContext;
import com.lamp.light.api.LightConstant;
import com.lamp.light.api.call.Callback;
import com.lamp.light.api.interceptor.Interceptor;
import com.lamp.light.api.response.Response;
import com.lamp.light.api.response.ReturnMode;
import com.lamp.light.handler.AsyncReturn;
import com.lamp.light.handler.DefaultCall;
import com.lamp.light.handler.Http11Factory;
import com.lamp.light.handler.Http11Factory.ChannelWrapper;
import com.lamp.light.model.ModelManage;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class NettyClient {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(NettyClient.class);
	
	private Map<ChannelId, AsyncReturn> channelIdToAsynReturn = new ConcurrentHashMap<>();

	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private Bootstrap bootstrap = new Bootstrap();
	
	private Bootstrap tlsBootstrap;
	
	private Executor executor; 

	private SslContext sslContext;
	
	
	
	public NettyClient(Executor executor) throws CertificateException, SSLException {
		this.executor = executor;
		init();
	}

	private void init( ) throws CertificateException, SSLException {
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LightIdleStateHandler());
				ch.pipeline().addLast(new HttpResponseDecoder());
				ch.pipeline().addLast(new HttpRequestEncoder());
				ch.pipeline().addLast(new HttpClientHandler());
			}
		});
	}
	
	private synchronized void createTLSBootstrap() throws Exception {
		
		if(Objects.nonNull(tlsBootstrap)) {
			return ;
		}
		
		SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
		SslContextBuilder sslContextBuilder = SslContextBuilder.forClient().sslProvider(SslProvider.JDK);
		sslContextBuilder.keyManager(selfSignedCertificate.certificate(),selfSignedCertificate.privateKey());
		sslContext = sslContextBuilder.build();
	
		tlsBootstrap.group(workerGroup);
		tlsBootstrap.channel(NioSocketChannel.class);
		tlsBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		tlsBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		tlsBootstrap.option(ChannelOption.TCP_NODELAY, true);
		tlsBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		tlsBootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));	
				ch.pipeline().addLast(new LightIdleStateHandler());
				ch.pipeline().addLast(new HttpResponseDecoder());
				ch.pipeline().addLast(new HttpRequestEncoder());
				ch.pipeline().addLast(new HttpClientHandler());
			}
		});
	}

	private Bootstrap getBootstrap(AsyncReturn asyncReturn) {
		try {
			if(asyncReturn.handleMethod().getRequestInfo().isTls()) {
				if(Objects.isNull(tlsBootstrap)) {
					synchronized(this) {
						if(Objects.isNull(tlsBootstrap)) {
							this.createTLSBootstrap();
						}
					}
				}
				return tlsBootstrap;
			}else {
				return bootstrap;
			}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Channel getChannle(AsyncReturn asyncReturn, InetSocketAddress inetSocketAddress) throws InterruptedException {
		return this.getBootstrap(asyncReturn).connect(inetSocketAddress).sync().channel();
	}

	public void write(AsyncReturn asyncReturn, InetSocketAddress inetSocketAddress) {
		if(asyncReturn.handleMethod().getRequestInfo().getProtocol().equals(LightConstant.PROTOCOL_HTTP_11)) {
			ChannelWrapper channelWrapper = Http11Factory.getInstance().getChannelWrapper(asyncReturn.handleMethod().isSecurity(), inetSocketAddress);
			if(Objects.nonNull(channelWrapper)) {
				
				channelWrapper.getChannel().writeAndFlush(asyncReturn.fullHttpRequest()).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (future.isSuccess()) {
							channelIdToAsynReturn.put(future.channel().id(), asyncReturn);
							asyncReturn.channelWrapper(channelWrapper);
						} else {
							error(asyncReturn, future.cause());
						}
					}
				});
				return;
			}
		}
		this.getBootstrap(asyncReturn).connect(inetSocketAddress).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					channelIdToAsynReturn.put(future.channel().id(), asyncReturn);
					future.channel().writeAndFlush(asyncReturn.fullHttpRequest());
					if(asyncReturn.handleMethod().getRequestInfo().getProtocol().equals(LightConstant.PROTOCOL_HTTP_11)) {
						ChannelWrapper channelWrapper = new ChannelWrapper();
						channelWrapper.setChannel(future.channel());
						channelWrapper.setAddress(inetSocketAddress);
						channelWrapper.setSecurity(asyncReturn.handleMethod().getRequestInfo().isTls());
						channelWrapper.setTimeout(asyncReturn.handleMethod().getRequestTimes());
						asyncReturn.channelWrapper(channelWrapper);
					}
				} else {
					error(asyncReturn, future.cause());
				}
			}
		});
		
    }

	public void error(AsyncReturn asyncReturn, Throwable throwable) {
		if (asyncReturn.returnMode().equals(ReturnMode.SYNS)) {
			try {
				Object object = ModelManage.getInstance().getModel(
						asyncReturn.handleMethod().getRequestInfo().getReturnClazz(), throwable, null, null);
				if (Objects.nonNull(object)) {
					asyncReturn.setObject(object);
					return;
				}
				asyncReturn.setObject(throwable);
				return;
			} catch (Exception e) {
				asyncReturn.setObject(e);
				return;
			}
		}else if (asyncReturn.returnMode().equals(ReturnMode.CALL)) {
			DefaultCall<Object> call = (DefaultCall<Object>) asyncReturn.call();
			call.setThrowable(throwable);
			if (Objects.isNull(call.getCallback())) {
				Callback<Object> callback = call.getCallback();
				callback.onFailure(call, asyncReturn.getArgs(), throwable);
			} else {
				asyncReturn.setObject(call);
			}
		}
		
		for(Interceptor interceptor:  asyncReturn.interceptorList()) {
			interceptor.handlerErrer(throwable, asyncReturn.handleMethod().getRequestInfo().requestWrapper(), asyncReturn.getArgs());
		}
	}

	class HttpClientHandler extends ChannelInboundHandlerAdapter {

		private DefaultHttpResponse defaultHttpResponse;

		// http1.1 http2.0是否可以做优化
		private ByteBuf connect;

		private AsyncReturn asyncReturn;

		private Throwable throwable;
		
		private Object object;

		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			asyncReturn = NettyClient.this.channelIdToAsynReturn.remove(ctx.pipeline().channel().id());
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg instanceof DecoderResultProvider) {
				DecoderResultProvider decoderResultProvider = (DecoderResultProvider) msg;
				DecoderResult decoderResult = decoderResultProvider.decoderResult();
				if (Objects.nonNull(decoderResult) && !decoderResult.isSuccess()) {
					// 异常
					throwable = decoderResult.cause();
				}
			}
			if (msg instanceof DefaultHttpResponse) {
				defaultHttpResponse = (DefaultHttpResponse) msg;
				HttpHeaders headers = defaultHttpResponse.headers();
				Integer contentLength = headers.getInt(HttpHeaderNames.CONTENT_LENGTH);
				connect = Objects.isNull(contentLength) ? Unpooled.buffer(8192) : Unpooled.buffer(contentLength);
			}

			if (msg instanceof LastHttpContent) {
				LastHttpContent lastHttpContent = (LastHttpContent) msg;
				connect.writeBytes(lastHttpContent.content());
				returnHandle(ctx);
			}
			if (msg instanceof HttpContent) {
				HttpContent content = (HttpContent) msg;
				if(Objects.nonNull(connect)) {
					connect.writeBytes(content.content());
				}
			}
		}

		private void returnHandle(ChannelHandlerContext ctx) {
			asyncReturn = NettyClient.this.channelIdToAsynReturn.remove(ctx.pipeline().channel().id());
			ChannelWrapper channelWrapper = asyncReturn.channelWrapper();
			if(asyncReturn.handleMethod().getRequestInfo().getProtocol().equals(LightConstant.PROTOCOL_HTTP_11)) {
				if(Objects.isNull(channelWrapper)) {
					ctx.close();
				}else {
					Http11Factory.getInstance().setChannelWrapper(channelWrapper);
				}
			}
			
			for(Interceptor interceptor : asyncReturn.interceptorList()) {
				interceptor.handlerResponse(defaultHttpResponse);
			}
			// 这里是否需要判断 http method方法
			boolean notReturn = Objects.isNull(asyncReturn.handleMethod().getRequestInfo().getReturnClazz());
				
			
			if(Objects.nonNull(asyncReturn.lightContext())) {
				LightContext.lightContext(asyncReturn.lightContext());
			}
			
			if(!Objects.equals(defaultHttpResponse.status(), HttpResponseStatus.OK)) {
				throwable = new RuntimeException(defaultHttpResponse.status().toString());
			}
			if (Objects.nonNull(throwable)) {
				object = ModelManage.getInstance().getModel(
							asyncReturn.handleMethod().getRequestInfo().getReturnClazz(), throwable,
							defaultHttpResponse, connect);
			}else {
				if(connect.writerIndex() > 0 ) {
					object = asyncReturn.serialize().deserialization(asyncReturn.handleMethod().getRequestInfo().getReturnClazz(),connect.array());
					this.release();
				}
			}
			
			if (asyncReturn.returnMode().equals(ReturnMode.SYNS) && !notReturn) {
				asyncReturn.setObject(Objects.nonNull(object)? object : throwable);
				handlerAfter();
				return;
			}
			if (asyncReturn.returnMode().equals(ReturnMode.CALL) || asyncReturn.returnMode().equals(ReturnMode.CALL_ASYNS)) {
				this.call();
				return;
			}
			if(asyncReturn.returnMode().equals(ReturnMode.ASYSN)) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							LightContext.lightContext(asyncReturn.lightContext());
							Response<Object> response = new Response<>(defaultHttpResponse);
							LightContext.lightContext().result(object, response);
							LightContext.lightContext().throwable(throwable, response);
							asyncReturn.handleMethod().getMethod().invoke(asyncReturn.handleMethod().getSuccess(), asyncReturn.getArgs());
						}catch(Exception e) {
							logger.error(e.getMessage(), e);
						}
						handlerAfter();
					}
				});
				return;
			}
		}
		
		private void handlerAfter() {
			try {
				for(Interceptor interceptor : asyncReturn.interceptorList()) {
					interceptor.handlerAfter(asyncReturn.handleMethod().getRequestInfo().requestWrapper(), defaultHttpResponse);
				}
			}catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		private void call() {
			DefaultCall<Object> call = (DefaultCall<Object>) asyncReturn.call();
			Response<Object> response = new Response<>(defaultHttpResponse);
			call.setResponse(response);
			call.setThrowable(throwable);
			call.setObject(object);
			if (Objects.nonNull(call.getCallback())) {
				Callback<Object> callback = call.getCallback();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							if (Objects.nonNull(object)) {
								callback.onResponse(call, asyncReturn.getArgs(), object);
							} else {
								callback.onFailure(call, asyncReturn.getArgs(), throwable);
							}
						}catch(Exception e) {
							logger.error(e.getMessage(),e);
						}
						handlerAfter();
					}
				});
				
			} else {
				asyncReturn.setObject(call);
				handlerAfter();
			}
			return;
		}
		
		private void release() {
			if(Objects.nonNull(connect)) {
				connect.release();
				connect=null;
			}
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if (IdleState.READER_IDLE.equals(evt) || IdleState.WRITER_IDLE.equals(evt) || IdleState.ALL_IDLE.equals(evt)) {
				AsyncReturn asyncReturn = NettyClient.this.channelIdToAsynReturn.get(ctx.pipeline().channel().id());
				NettyClient.this.error(asyncReturn, new RuntimeException("request timeout"));
			}
			this.release();
			ctx.close();
		}

		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			AsyncReturn asyncReturn = NettyClient.this.channelIdToAsynReturn.get(ctx.pipeline().channel().id());
			NettyClient.this.error(asyncReturn, new RuntimeException("request timeout"));
			this.release();
			ctx.close();
		}

	}

	class LightIdleStateHandler extends ChannelDuplexHandler {

		private IdleStateHandler idleStateHandler;

		private IdleStateHandler getIdleStateHandler(ChannelHandlerContext ctx) throws Exception {
			if (Objects.isNull(idleStateHandler)) {
				AsyncReturn asyncReturn = NettyClient.this.channelIdToAsynReturn.get(ctx.pipeline().channel().id());
				// 得到时间 asynReturn.getRequestTimes()
				if(Objects.isNull(asyncReturn)) {
					return null;
				}
				idleStateHandler = new IdleStateHandler(asyncReturn.requestTimes(), asyncReturn.requestTimes(), asyncReturn.requestTimes());
				idleStateHandler.handlerAdded(ctx);
			}
			return idleStateHandler;
		}

		@Override
		public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
			IdleStateHandler idleStateHandler = getIdleStateHandler(ctx);
			if(Objects.nonNull(idleStateHandler)) {
				idleStateHandler.handlerAdded(ctx);
			}
		}

		@Override
		public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
			getIdleStateHandler(ctx).handlerRemoved(ctx);
		}

		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			IdleStateHandler idleStateHandler = getIdleStateHandler(ctx);
			if(Objects.nonNull(idleStateHandler)) {
				idleStateHandler.channelRegistered(ctx);
			}
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			IdleStateHandler idleStateHandler = getIdleStateHandler(ctx);
			if(Objects.nonNull(idleStateHandler)) {
				idleStateHandler.channelActive(ctx);
			}
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			IdleStateHandler idleStateHandler = getIdleStateHandler(ctx);
			if(Objects.nonNull(idleStateHandler)) {
				idleStateHandler.channelInactive(ctx);
			}
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			getIdleStateHandler(ctx).channelRead(ctx, msg);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			getIdleStateHandler(ctx).channelReadComplete(ctx);
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			getIdleStateHandler(ctx).write(ctx, msg, promise);
		}

	}
}
