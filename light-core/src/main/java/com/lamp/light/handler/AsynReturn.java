package com.lamp.light.handler;

import java.util.concurrent.locks.StampedLock;

import com.lamp.light.serialize.Serialize;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;

public class AsynReturn {

    private Channel channel;
    
    private Object[] args;
    
    private StampedLock stampedLock = new StampedLock();
    
    private Serialize serialize;
    
    private FullHttpRequest fullHttpRequest;
    
    public AsynReturn() {
        
    }

    public Channel getChannel() {
        return channel;
    }

    public Object[] getArgs() {
        return args;
    }

    public StampedLock getStampedLock() {
        return stampedLock;
    }

    public Serialize getSerialize() {
        return serialize;
    }

    public FullHttpRequest getFullHttpRequest() {
        return fullHttpRequest;
    }
    
    
    
}
