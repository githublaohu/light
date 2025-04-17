package com.lamp.light.data.local;

import com.lamp.electron.register.api.RegisterModel;

public class LocalRegister implements RegisterModel<Object> {

    @Override
    public int register(Object o) {
        return 0;
    }

    @Override
    public int deregister(Object o) {
        return 0;
    }
}
