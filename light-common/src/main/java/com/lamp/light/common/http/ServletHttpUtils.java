package com.lamp.light.common.http;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import java.util.Objects;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author laohu
 */
public class ServletHttpUtils {


    public static String  getSpotData(HttpServletRequest servletRequest , HttpDataSpot spot, String key){
        if(Objects.equals(spot , HttpDataSpot.HEADER)){
            return servletRequest.getHeader(key);
        }else if (Objects.equals(spot , HttpDataSpot.COOKIE)){
            Cookie[] cookies = servletRequest.getCookies();
            for(Cookie cookie : cookies){
                if(Objects.equals(cookie.getName(), key)){
                    return cookie.getValue();
                }
            }
            return null;
        }else if(Objects.equals(spot , HttpDataSpot.FORM)){
            return servletRequest.getParameter(key);
        }else if(Objects.equals(spot , HttpDataSpot.QUERY)){
            return null;
        }

        return null;
    }

    public static void setSpotData(HttpServletResponse servletResponse , HttpDataSpot spot, String key, Object value){
        if(Objects.equals(spot , HttpDataSpot.HEADER)){
            servletResponse.setHeader(key , (String)value);
        }else if (Objects.equals(spot , HttpDataSpot.COOKIE)){
            servletResponse.addCookie((Cookie) value);
        }
    }
}
