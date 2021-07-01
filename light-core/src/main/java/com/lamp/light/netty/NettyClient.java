/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PSL v2.
 *You can use this software according to the terms and conditions of the Mulan PSL v2.
 *You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PSL v2 for more details.
 */
package com.lamp.light.netty;

import com.lamp.light.Callback;
import com.lamp.light.handler.AsynReturn;
import com.lamp.light.handler.DefaultCall;
import com.lamp.light.response.Response;
import com.lamp.light.response.ReturnMode;
import com.lamp.ligth.model.ModelManage;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class NettyClient {

	private Map<ChannelId, AsynReturn> channelIdToAsynReturn = new ConcurrentHashMap<>();

	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private Bootstrap b = new Bootstrap();
	
	private Executor executor; 

	public NettyClient( Executor executor) {
		this.executor = executor;
		init();
	}

	private void init() {
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		b.option(ChannelOption.TCP_NODELAY, true);
		b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LightIdleStateHandler());
				ch.pipeline().addLast(new HttpResponseDecoder());
				ch.pipeline().addLast(new HttpRequestEncoder());
				ch.pipeline().addLast(new HttpClientHandler());
			}
		});
	}

	public Channel getChannle(InetSocketAddress inetSocketAddress) throws InterruptedException {
		return b.connect(inetSocketAddress).sync().channel();
	}

	public void write(AsynReturn asynReturn, InetSocketAddress inetSocketAddress) {
        b.connect(inetSocketAddress).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    channelIdToAsynReturn.put(future.channel().id(), asynReturn);
                    future.channel().writeAndFlush(asynReturn.getFullHttpRequest());
                } else {
                	error(asynReturn, future.cause());
                }
            }
        });
    }

	public void error(AsynReturn asynReturn, Throwable throwable) {
		if (asynReturn.getReturnMode().equals(ReturnMode.SYNS)) {
			try {
				Object object = ModelManage.getInstance().getModel(
						asynReturn.getHandleMethod().getRequestInfo().getReturnClazz(), throwable, null, null);
				if (Objects.nonNull(object)) {
					asynReturn.setObject(object);
					return;
				}
				asynReturn.setObject(throwable);
				return;
			} catch (Exception e) {
				asynReturn.setObject(e);
				return;
			}
		}else if (asynReturn.getReturnMode().equals(ReturnMode.CALL)) {
			DefaultCall<Object> call = (DefaultCall<Object>) asynReturn.getCall();
			call.setThrowable(throwable);
			if (Objects.isNull(call.getCallback())) {
				Callback<Object> callback = call.getCallback();
				callback.onFailure(call, asynReturn.getArgs(), throwable);
			} else {
				asynReturn.setObject(call);
			}
		}
	}

	class HttpClientHandler extends ChannelInboundHandlerAdapter {

		private DefaultHttpResponse defaultHttpResponse;

		private ByteBuf connect;

		private AsynReturn asynReturn;

		private Throwable throwable;

		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			asynReturn = NettyClient.this.channelIdToAsynReturn.remove(ctx.pipeline().channel().id());
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
				ctx.close();
				returnHandle(ctx);
			}

			if (msg instanceof HttpContent) {
				HttpContent content = (HttpContent) msg;
				connect.writeBytes(content.content());
			}

		}

		private void returnHandle(ChannelHandlerContext ctx) {
			asynReturn = NettyClient.this.channelIdToAsynReturn.remove(ctx.pipeline().channel().id());
			if (asynReturn.getReturnMode().equals(ReturnMode.SYNS)) {
				if (Objects.nonNull(throwable)
						|| !Objects.equals(defaultHttpResponse.status(), HttpResponseStatus.OK)) {
					try {
						Object object = ModelManage.getInstance().getModel(
								asynReturn.getHandleMethod().getRequestInfo().getReturnClazz(), throwable,
								defaultHttpResponse, connect);
						if (Objects.nonNull(object)) {
							asynReturn.setObject(object);
							return;
						}
						asynReturn.setObject(throwable);
						return;
					} catch (Exception e) {
						asynReturn.setObject(e);
						return;
					}
				}
				Object object = asynReturn.getSerialize().deserialization(
						asynReturn.getHandleMethod().getRequestInfo().getReturnClazz(), connect.array());
				asynReturn.setObject(object);
				return;
			}
			if (asynReturn.getReturnMode().equals(ReturnMode.CALL)) {
				DefaultCall<Object> call = (DefaultCall<Object>) asynReturn.getCall();
				Response<Object> response = new Response<>(defaultHttpResponse);
				call.setResponse(response);
				call.setThrowable(throwable);
				if (Objects.isNull(call.getCallback())) {
					Callback<Object> callback = call.getCallback();
					executor.execute(new Runnable() {
						@Override
						public void run() {
							if (Objects.isNull(throwable)) {
								// 这里应该是异步
								Object object = asynReturn.getSerialize().deserialization(asynReturn.getClass(),
										connect.array());
								callback.onResponse(call, asynReturn.getArgs(), object);
							} else {
								callback.onFailure(call, asynReturn.getArgs(), throwable);
							}
						}
					});
					
				} else {
					asynReturn.setObject(call);
				}
			}
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if (IdleState.READER_IDLE.equals(evt)) {
				AsynReturn asynReturn = NettyClient.this.channelIdToAsynReturn.get(ctx.pipeline().channel().id());
				NettyClient.this.error(asynReturn, new RuntimeException("request timeout"));
			}
			ctx.close();
		}

		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		}

	}

	class LightIdleStateHandler extends ChannelDuplexHandler {

		private IdleStateHandler idleStateHandler;

		private IdleStateHandler getIdleStateHandler(ChannelHandlerContext ctx) throws Exception {
			if (Objects.isNull(idleStateHandler)) {
				AsynReturn asynReturn = NettyClient.this.channelIdToAsynReturn.get(ctx.pipeline().channel().id());
				// 得到时间 asynReturn.getRequestTimes()
				idleStateHandler = new IdleStateHandler(asynReturn.getRequestTimes(), -1, -1);
				idleStateHandler.handlerAdded(ctx);
			}
			return idleStateHandler;
		}

		@Override
		public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
			// getIdleStateHandler(ctx).handlerAdded(ctx);
		}

		@Override
		public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
			// getIdleStateHandler(ctx).handlerAdded(ctx);
		}

		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			// getIdleStateHandler(ctx).channelRegistered(ctx);
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			getIdleStateHandler(ctx).channelActive(ctx);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			getIdleStateHandler(ctx).channelInactive(ctx);
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
