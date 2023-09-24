package com.lamp.light.common.validate.code;

import com.lamp.light.api.validate.code.ValidateCode;
import java.util.concurrent.ThreadLocalRandom;

public class StringRandonSerice extends  AbstractRandomService{

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
    public String createRandom(ValidateCode validateCode) {
        return generateRandomString(this.validateCodeConfig.getLength());
    }
}
