package com.lamp.light.common.validate.code;

import com.lamp.light.api.validate.code.ValidateCodeConfig;

public abstract class AbstractRandomService implements  RandomService {

    protected ValidateCodeConfig validateCodeConfig;

    public void init(ValidateCodeConfig validateCodeConfig){
        this.validateCodeConfig = validateCodeConfig;
    }

    protected  abstract void doInit();
}
