package com.lamp.light.data.local;

import com.lamp.electron.register.api.RegisterData;
import com.lamp.electron.register.api.RegisterModel;
import com.lamp.electron.register.api.RegisterObjectFactory;
import com.lamp.electron.register.api.RegisterServer;

public class LocalRegisterObjectFactory  implements RegisterObjectFactory {

    @Override
    public String registerCenterName() {
        return "local";
    }

    @Override
    public RegisterModel<Object> createRegisterModel(RegisterServer<Object> registerServers, RegisterData registerData) {
        LocalRegister localRegister = new LocalRegister();
        localRegister.registerData = registerData;
        localRegister.registerServers = registerServers;
        return localRegister;
    }

    public class LocalRegister implements RegisterModel<Object> {

        RegisterServer<Object> registerServers;

        RegisterData registerData;

        @Override
        public int register(Object o) {
            return 0;
        }

        @Override
        public int deregister(Object o) {
            return 0;
        }
    }

}
