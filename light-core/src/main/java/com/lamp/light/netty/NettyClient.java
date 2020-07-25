package com.lamp.light.netty;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.lamp.light.handler.AsynReturn;
import com.lamp.light.handler.DefaultCall;
import com.lamp.light.response.Response;
import com.lamp.light.response.ReturnMode;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
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
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClient {

    private Map<ChannelId, AsynReturn> channelIdToAsynReturn = new ConcurrentHashMap<>();

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Bootstrap b = new Bootstrap();

    public NettyClient() {
        init();
    }

    private void init() {
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
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

                }
            }
        });
    }

    class HttpClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            AsynReturn asynReturn = NettyClient.this.channelIdToAsynReturn.remove(ctx.pipeline().channel().id());
            if (msg instanceof DefaultFullHttpResponse) {
                DefaultFullHttpResponse response = (DefaultFullHttpResponse)msg;

            } else if (msg instanceof DefaultHttpResponse) {
                DefaultHttpResponse response = (DefaultHttpResponse)msg;
                response.headers();
                asynReturn.setObject(response);

            }

            if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent)msg;
                ByteBuf buf = content.content();
                System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
                buf.release();
            }
            if (asynReturn.getReturnMode().equals(ReturnMode.SYNS)) {
                asynReturn.setObject(null);
            }
            if (asynReturn.getReturnMode().equals(ReturnMode.CALL)) {
                DefaultCall<Object> call = (DefaultCall<Object>)asynReturn.getCall();
                Response<Object> response = new Response<>((HttpResponse)msg , asynReturn.getSerialize());
                call.setResponse(response);
                if (Objects.isNull(call.getCallback())) {
                     
                } else {
                    asynReturn.setObject(call);
                }
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            ctx.fireUserEventTriggered(evt);
        }

    }

    class LightIdleStateHandler extends ChannelDuplexHandler {

        private IdleStateHandler idleStateHandler;

        private IdleStateHandler getIdleStateHandler(ChannelHandlerContext ctx) throws Exception {
            if (Objects.isNull(idleStateHandler)) {
                AsynReturn asynReturn = NettyClient.this.channelIdToAsynReturn.get(ctx.pipeline().channel().id());
                // 得到时间
                idleStateHandler = new IdleStateHandler(-1, -1, asynReturn.getRequestTimes());
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
