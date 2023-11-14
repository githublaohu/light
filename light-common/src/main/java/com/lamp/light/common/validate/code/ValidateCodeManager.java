package com.lamp.light.common.validate.code;

import com.lamp.light.api.validate.code.RandomService;
import com.lamp.light.api.validate.code.ValidateCode;
import com.lamp.light.api.validate.code.ValidateCodeConfig;
import com.lamp.light.api.validate.code.VerifyResourceResult;
import com.lamp.light.api.validate.code.verify.ImageInformation;
import com.lamp.light.common.cache.CacheConfig;
import com.lamp.light.common.validate.code.service.CacheProxyRandomService;
import com.lamp.light.common.validate.code.service.ChainRandomService;
import com.lamp.light.common.validate.code.service.StorageRandomService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
public class ValidateCodeManager implements RandomService {

    private Map<String, ValidateCodeConfig> validateCodeMap = new ConcurrentHashMap<>();

    private VerifyService verifyService;

    private Map<String, CacheConfig> cacheConfigMap = new ConcurrentHashMap<>();

    private Map<String,ChainRandomService> randomServiceMap = new ConcurrentHashMap<>();


    private Map<String,ChainRandomService> bottomMap = new ConcurrentHashMap<>();

    public void createVerifyService(List<ImageInformation> imageInformationList){
        verifyService = new VerifyService(imageInformationList);
    }

    public void cacheConfig(){

    }

    public void putRandomService(String name , ChainRandomService randomService){
        randomService.setValidateCodeConfig(validateCodeMap.get(name));
        ChainRandomService chainRandomService = bottomMap.get(name);
        if(Objects.nonNull(chainRandomService)){
            chainRandomService.setRandomService(randomService);
            bottomMap.put(name,chainRandomService);
        }else{
            randomServiceMap.put(name , randomService);
        }
    }

    public void init(){
        validateCodeMap.forEach((key,value)->{
            ChainRandomService chainRandomService = bottomMap.get(key);
            if(Objects.nonNull(value.getCacheName())){
                CacheProxyRandomService cacheProxyRandomService = new CacheProxyRandomService();

                cacheProxyRandomService.setValidateCodeConfig(value);

                if(Objects.nonNull(chainRandomService)){
                    chainRandomService.setRandomService(cacheProxyRandomService);
                }else{
                    randomServiceMap.put(key , cacheProxyRandomService);
                }
                chainRandomService = cacheProxyRandomService;
            }
            ChainRandomService chainVerifyService = new ChainRandomService();
            chainVerifyService.setRandomService(verifyService);
            if(Objects.nonNull(chainRandomService)){
                chainRandomService.setRandomService(chainVerifyService);
            }else{
                randomServiceMap.put(key , chainVerifyService);
            }

        });

    }




    @Override
    public VerifyResourceResult createRandom(ValidateCode validateCode) {
        ChainRandomService chainRandomService = randomServiceMap.get(validateCode.getName());
        return chainRandomService.createRandom(validateCode);
    }
}
