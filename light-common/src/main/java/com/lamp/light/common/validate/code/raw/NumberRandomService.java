package com.lamp.light.common.validate.code.raw;

import com.lamp.light.api.validate.code.ValidateCode;
import com.lamp.light.api.validate.code.VerifyResourceResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class NumberRandomService extends AbstractRandomService {

    public static final Map<Integer, String> SUPPLEMENT_STRING = new HashMap<>();

    static {
        String value = "";
        for (int i = 1; i < 12; i++) {
            value = value + "0";
            SUPPLEMENT_STRING.put(i, value);
        }
    }

    private int max;

    private int supplement;

    @Override
    public VerifyResourceResult createRandom(ValidateCode validateCode) {
        int value = ThreadLocalRandom.current().nextInt(max);
        String valueString = Integer.toString(value);
        // 如果数值小于长度，就补零
        if (value < supplement) {
            //valueString = SUPPLEMENT_STRING.get(this.validateCodeConfig.getLength() - valueString.length()) + valueString;
        }
        return null;
    }

    @Override
    protected void doInit() {

    }
}
