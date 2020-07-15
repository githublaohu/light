package com.lamp.light.util;

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
}
