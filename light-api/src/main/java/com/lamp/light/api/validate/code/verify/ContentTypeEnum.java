package com.lamp.light.api.validate.code.verify;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
public enum ContentTypeEnum {

    DEFAULT(1, "字母数字混合", "图片内容为字母数字混合"),
    ONLY_NUM(2, "纯数字", "图片内容为纯数字"),
    ONLY_CHAR(3, "纯字母", "图片内容为大小写字母混合"),
    ONLY_UPPER_CHAR(4, "大写字母", "图片内容为大写字母"),
    ONLY_LOWER_CHAR(5, "小写字母", "图片内容为小写字母"),
    NUM_AND_CHAR(6, "数字大写字母混合", "图片内容为数字字母混合");

    private int index;

    private String name;

    private String description;


    ContentTypeEnum(int index, String name, String description) {
        this.index = index;
        this.name = name;
        this.description = description;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
