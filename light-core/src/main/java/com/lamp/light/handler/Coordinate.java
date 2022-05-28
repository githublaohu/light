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
package com.lamp.light.handler;

import java.lang.reflect.Method;

public class Coordinate {

    private int index;

    private String key;

    private Method method;

    private ParametersType type;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ParametersType getType() {
        return type;
    }

    public void setType(ParametersType type) {
        this.type = type;
    }

    public enum ParametersType {

        BASIC, PACKING, STRING, MAP, LIST, LIST_MAP, LIST_OBJECT, OBJECT, UPLOAD,UPLOAD_LIST,UPLOAD_MAP,;
    }
}
