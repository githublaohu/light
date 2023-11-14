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


import com.lamp.light.api.validate.code.RandomService;
import com.lamp.light.api.validate.code.ValidateCode;
import com.lamp.light.api.validate.code.VerifyResourceResult;
import com.lamp.light.api.validate.code.verify.ImageInformation;
import com.lamp.light.api.validate.code.verify.RandomImageStyle;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author 13759
 */
public class VerifyService implements RandomService {

    private static final Map<RandomImageStyle, Class<?>> NAME_TO_CLASS_MAP = new ConcurrentHashMap<>();

    static {
        NAME_TO_CLASS_MAP.put(RandomImageStyle.PNG, PngCreateCaptcha.class);
        NAME_TO_CLASS_MAP.put(RandomImageStyle.CHINESE, CnCreateCaptcha.class);
        NAME_TO_CLASS_MAP.put(RandomImageStyle.GIF, GifCreateCaptcha.class);
        NAME_TO_CLASS_MAP.put(RandomImageStyle.CHINESEGIF, CnGifCreateCaptcha.class);
        NAME_TO_CLASS_MAP.put(RandomImageStyle.ARITHMETIC, ArithmeticCreateCaptcha.class);
    }

    private final Map<String, AbstractCreateCaptcha> createCaptchaMap = new ConcurrentHashMap<>();


    public VerifyService(List<ImageInformation> imageInformationList)  {
        try {
            for (ImageInformation imageInformation : imageInformationList) {
                AbstractCreateCaptcha abstractCreateCaptcha = (AbstractCreateCaptcha) NAME_TO_CLASS_MAP.get(imageInformation.getRandomImageStyle()).newInstance();
                abstractCreateCaptcha.imageInformation = imageInformation;
                createCaptchaMap.put(imageInformation.getName(), abstractCreateCaptcha);
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }
    }


    @Override
    public VerifyResourceResult createRandom(ValidateCode validateCode) {
        AbstractCreateCaptcha createCaptcha = createCaptchaMap.get(validateCode.getName());
        Captcha captcha = createCaptcha.creatCaptcha();
        VerifyResourceResult verifyResourceResult = new VerifyResourceResult();
        verifyResourceResult.setImageCode(captcha.text());
        if (captcha instanceof ArithmeticCaptcha) {
            // 获取运算的公式：3+2=?
            verifyResourceResult.setImageContent(((ArithmeticCaptcha) captcha).getArithmeticString());
        }
        if (!createCaptcha.imageInformation.isOnlyText()) {
            verifyResourceResult.setImageType(createCaptcha.imageType());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            captcha.out(outputStream);
            verifyResourceResult.setImageByte(outputStream);
        }
        return verifyResourceResult;
    }

    interface CreateCaptcha {
        Captcha creatCaptcha();

        String imageType();
    }

    static abstract class AbstractCreateCaptcha implements CreateCaptcha {
        ImageInformation imageInformation;

        public abstract Captcha doCreatCaptcha();

        @Override
        public String imageType() {
            return "image/gif";
        }

        @Override
        public Captcha creatCaptcha() {
            Captcha captcha = this.doCreatCaptcha();
            captcha.setCharType(imageInformation.getContentType().getIndex());
            captcha.setLen(imageInformation.getLen());
            captcha.setWidth(imageInformation.getWidth());
            captcha.setHeight(imageInformation.getHeight());
            captcha.setFont(new Font(imageInformation.getFontFamily(), imageInformation.getFontStyle(), imageInformation.getFontSize()));

            return captcha;
        }
    }

    static class PngCreateCaptcha extends AbstractCreateCaptcha {

        @Override
        public String imageType() {
            return "image/png";
        }

        @Override
        public Captcha doCreatCaptcha() {
            return new SpecCaptcha();
        }
    }

    static class GifCreateCaptcha extends AbstractCreateCaptcha {

        @Override
        public Captcha doCreatCaptcha() {
            return new GifCaptcha();
        }
    }

    static class CnCreateCaptcha extends AbstractCreateCaptcha {

        @Override
        public Captcha doCreatCaptcha() {
            return new ChineseCaptcha();
        }
    }

    static class CnGifCreateCaptcha extends AbstractCreateCaptcha {

        @Override
        public Captcha doCreatCaptcha() {
            return new ChineseCaptcha();
        }
    }

    static class ArithmeticCreateCaptcha extends AbstractCreateCaptcha {

        @Override
        public Captcha doCreatCaptcha() {
            return new ArithmeticCaptcha();
        }
    }


}

