package com.lamp.light.api.validate.code.verify;


import java.awt.*;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */

public class ImageInformation {

    private RandomImageStyle randomImageStyle;

    private ContentTypeEnum contentType;

    private String name;

    private boolean onlyText = true;

    private int onlyNum = 0;

    private int width = 130;

    private int height = 48;

    private int len = 5;

    private String fontFamily = "楷体";

    private int fontStyle = Font.PLAIN;

    private int fontSize = 28;


    public RandomImageStyle getRandomImageStyle() {
        return randomImageStyle;
    }

    public void setRandomImageStyle(RandomImageStyle randomImageStyle) {
        this.randomImageStyle = randomImageStyle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnlyText() {
        return onlyText;
    }

    public void setOnlyText(boolean onlyText) {
        this.onlyText = onlyText;
    }

    public int getOnlyNum() {
        return onlyNum;
    }

    public void setOnlyNum(int onlyNum) {
        this.onlyNum = onlyNum;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public ContentTypeEnum getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }
}
