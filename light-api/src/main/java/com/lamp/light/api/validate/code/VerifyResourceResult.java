package com.lamp.light.api.validate.code;

import java.io.OutputStream;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */


public class VerifyResourceResult {


    private String imageType;

    private String imageCode;

    private String imageContent;

    private OutputStream imageByte;

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }


    public OutputStream getImageByte() {
        return imageByte;
    }

    public void setImageByte(OutputStream imageByte) {
        this.imageByte = imageByte;
    }
}
