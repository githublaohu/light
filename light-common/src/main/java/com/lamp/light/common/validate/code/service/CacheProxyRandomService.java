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
package com.lamp.light.common.validate.code.service;

import com.lamp.light.api.validate.code.ValidateCode;
import com.lamp.light.api.validate.code.ValidateCodeConfig;
import com.lamp.light.api.validate.code.VerifyResourceResult;
import com.lamp.light.api.validate.code.RandomService;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
public class CacheProxyRandomService extends ChainRandomService {


    @Override
    public VerifyResourceResult createRandom(ValidateCode validateCode) {

        // 是否重复

        //
        VerifyResourceResult verifyResourceResult = this.randomService.createRandom(validateCode);

        // 缓存

        return verifyResourceResult;
    }
}
