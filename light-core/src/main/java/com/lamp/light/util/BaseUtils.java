/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PSL v2.
 *You can use this software according to the terms and conditions of the Mulan PSL v2.
 *You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PSL v2 for more details.
 */
package com.lamp.light.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Objects;

public class BaseUtils {

    
    public static String setSlash(String str) {
        if(Objects.isNull(str)) {
            return "";
        }
        
        if(Objects.equals("", str)) {
            return str;
        }
        
        if(str.charAt(0) == '/') {
            return str;
        }
        return "/"+str;
    }
    
    //method.getGenericReturnType()
    public static Type getParameterUpperBound(int index, ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        if (index < 0 || index >= types.length) {
          throw new IllegalArgumentException(
              "Index " + index + " not in range [0," + types.length + ") for " + type);
        }
        Type paramType = types[index];
        if (paramType instanceof WildcardType) {
          return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
      }
}
