package com.lamp.light.common.validate.code.raw;

import com.lamp.light.api.validate.code.ValidateCode;
import com.lamp.light.api.validate.code.VerifyResourceResult;

import java.util.concurrent.ThreadLocalRandom;

public class StringRandonSerice extends AbstractRandomService {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    @Override
    public VerifyResourceResult createRandom(ValidateCode validateCode) {
        return null;//generateRandomString(this.validateCodeConfig.getLength());
    }

    @Override
    protected void doInit() {

    }
}
