package com.lamp.light.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.lamp.light.handler.AsynReturn;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;

public class NettyClient {

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
                // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码  
                ch.pipeline().addLast(new HttpResponseDecoder());  
                // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码  
                ch.pipeline().addLast(new HttpRequestEncoder());  
                ch.pipeline().addLast(new HttpClientHandler());
            }  
        });  
    }
    
    public Channel getChannle(InetSocketAddress inetSocketAddress) throws InterruptedException {
            return b.connect(inetSocketAddress).sync().channel();  
    }
    
    public void write(AsynReturn asynReturn  ,InetSocketAddress inetSocketAddress) {
        b.connect(inetSocketAddress).addListener( new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().writeAndFlush(asynReturn);
            }
        });
    }
    
    static class HttpClientHandler extends ChannelInboundHandlerAdapter implements ChannelOutboundHandler{
        
        private AsynReturn asynReturn;
        
        @Override  
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
            if (msg instanceof HttpResponse)   
            {  
                HttpResponse response = (HttpResponse) msg;  
                System.out.println("CONTENT_TYPE:" + response.headers().get(HttpHeaders.Names.CONTENT_TYPE));  
            }  
            if(msg instanceof HttpContent)  
            {  
                HttpContent content = (HttpContent)msg;  
                ByteBuf buf = content.content();  
                System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));  
                buf.release();  
            }  
        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise)
            throws Exception {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
            ChannelPromise promise) throws Exception {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void read(ChannelHandlerContext ctx) throws Exception {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if(msg instanceof AsynReturn) {
                asynReturn = (AsynReturn)asynReturn;
                ctx.write(asynReturn.getFullHttpRequest());
            }else {
                
            }
            
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            // TODO Auto-generated method stub
            
        }  
    }
}
