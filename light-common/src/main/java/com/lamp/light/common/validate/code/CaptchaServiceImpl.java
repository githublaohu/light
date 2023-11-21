/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PubL v2.
 *You can use this software according to the terms and conditions of the Mulan PubL v2.
 *You may obtain a copy of Mulan PubL v2 at:
 *         http://license.coscl.org.cn/MulanPubL-2.0
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PubL v2 for more details.
 */
package com.lamp.light.common.validate.code;

import com.google.code.kaptcha.Producer;

import java.awt.image.BufferedImage;


public class CaptchaServiceImpl implements CaptchaService {

    private Producer producer;

    private String defaultCaptcha;

    /**
     * 生成并缓存验证码，返给前端图片
     */

    @Override
    public BufferedImage getCaptcha(String uuid) {
        //生成文字验证码
        String code = producer.createText();
        return producer.createImage(code);
    }
}

//    /**
//     * 校验验证码
//     */
//    @Override
//    public boolean validate(String uuid, String code) {
//
//        //测试环境123456通过验证（可不加）
//        if (EnvEnum.dev.name().equals(env) && code.equals(defaultCaptcha)) {
//            return true;
//        }
//
//        String cacheCode = redisService.get(AuthKeys.AUTH_CAPTCHA, uuid, String.class);
//        if (StringUtils.isEmpty(cacheCode)) {
//            return false;
//        }
//        //删除缓存验证码
//        redisService.delete(AuthKeys.AUTH_CAPTCHA, uuid);
//        if (cacheCode.equalsIgnoreCase(code)) {
//            return true;
//        }
//        return false;
//    }
