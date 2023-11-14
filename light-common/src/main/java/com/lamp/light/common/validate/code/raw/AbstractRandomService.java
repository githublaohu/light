package com.lamp.light.common.validate.code.raw;

import com.lamp.light.api.validate.code.ValidateCodeConfig;
import com.lamp.light.api.validate.code.RandomService;

public abstract class AbstractRandomService implements RandomService {

    protected ValidateCodeConfig validateCodeConfig;

    public void init(ValidateCodeConfig validateCodeConfig) {
        this.validateCodeConfig = validateCodeConfig;
    }

    protected abstract void doInit();
}
