package com.lamp.light.common.validate.code;

import com.lamp.light.api.validate.code.RandomEnum;
import com.lamp.light.api.validate.code.ValidateCode;
import com.lamp.light.api.validate.code.ValidateCodeConfig;
import com.lamp.light.api.validate.code.ValidateCodeManager;
import com.lamp.light.api.validate.code.ValidateCodePersistenceService;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValidateCodeManagerImpl implements ValidateCodeManager {


    private static final Map<RandomEnum, Class<?>> RADMON_CLASS = new HashMap<>();

    static {
        RADMON_CLASS.put(RandomEnum.NUMBER, NumberRandomService.class);
        RADMON_CLASS.put(RandomEnum.STRING, StringRandonSerice.class);
        RADMON_CLASS.put(RandomEnum.GROUP, RandomGroupService.class);

    }

    private Map<String/** system  **/, Map<String /** domain **/, Map<String/*drop*/, AbstractRandomService>>>
            randomServiceMap = new ConcurrentHashMap<>();

    private ValidateCodePersistenceService validateCodePersistenceService;

    public void r(ValidateCodeConfig validateCodeConfig)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = RADMON_CLASS.get(validateCodeConfig.getType());

        AbstractRandomService randomService = (AbstractRandomService) clazz.getDeclaredConstructor().newInstance();
        randomService.init(validateCodeConfig);

        randomServiceMap.computeIfAbsent(validateCodeConfig.getSystem(), (k) -> new ConcurrentHashMap<>())
                .computeIfAbsent(validateCodeConfig.getDomain(), (k) -> new ConcurrentHashMap<>())
                .computeIfAbsent(validateCodeConfig.getDrop(), (k) -> randomService);
    }


    private String createKey() {

        return "";
    }

    @Override
    public void createValidateCode(ValidateCode validateCode) {
        AbstractRandomService randomService =
                randomServiceMap.get(validateCode.getSystem()).get(validateCode.getDomain())
                        .get(validateCode.getDrop());
        String key ="";
        for ( ; ; ) {
            // 获得随机
            randomService.createRandom(validateCode);
            // uniqueness 验证唯一性

        }

        // 保存数据，使用异步，提供接口，
       // validateCodePersistenceService.installValidateCode();

    }

    @Override
    public void validateCode() {

    }


}
