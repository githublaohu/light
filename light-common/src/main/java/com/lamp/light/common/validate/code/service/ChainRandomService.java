package com.lamp.light.common.validate.code.service;

import com.lamp.light.api.validate.code.RandomService;
import com.lamp.light.api.validate.code.ValidateCode;
import com.lamp.light.api.validate.code.ValidateCodeConfig;
import com.lamp.light.api.validate.code.VerifyResourceResult;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
public  class ChainRandomService implements RandomService {


    RandomService randomService;

    ValidateCodeConfig validateCodeConfig;

    public void setValidateCodeConfig(ValidateCodeConfig validateCodeConfig) {
        this.validateCodeConfig = validateCodeConfig;
    }

    public void setRandomService(RandomService randomService) {
        this.randomService = randomService;
    }

    @Override
    public VerifyResourceResult createRandom(ValidateCode validateCode) {
        return this.randomService.createRandom(validateCode);
    }
}
