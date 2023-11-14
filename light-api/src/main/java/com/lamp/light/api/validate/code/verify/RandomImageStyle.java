package com.lamp.light.api.validate.code.verify;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
public enum RandomImageStyle {

    //图片验证码为算术表达式，且为PNG格式
    ARITHMETIC,

    //图片验证码为中文，且为PNG格式
    CHINESE,

    //图片验证码为中文，且为GIF格式
    CHINESEGIF,

    //图片格式为GIF
    GIF,

    //图片格式为PNG
    PNG
}
