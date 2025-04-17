package com.lamp.light.api.data.register;

import java.util.ArrayList;
import java.util.List;

public class MultipleProxyRegisterServer implements RegisterServer<Object> {

    List<RegisterServer> registerServerList = new ArrayList<>();


    public synchronized void addRegisterServer(RegisterServer registerServer) {
        List<RegisterServer> registerServerList = new ArrayList<>();
        registerServerList.addAll(this.registerServerList);
        registerServerList.add(registerServer);
    }

    @Override
    public int register(Object o) {
        List<RegisterServer> registerServerList = this.registerServerList;
        registerServerList.forEach((v) -> {
            v.register(o);
        });
        return 0;
    }

    @Override
    public int deregister(Object o) {
        List<RegisterServer> registerServerList = this.registerServerList;
        registerServerList.forEach((v) -> {
            v.deregister(o);
        });
        return 0;
    }
}
