package com.github.youzan.httpfetch.bookworm.vo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by daiqiang on 17/6/14.
 */
public class UploadFileResponseVo {

    private String fileName;

    private String name;

    @JSONField(name="n_value")
    private String nValue;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getnValue() {
        return nValue;
    }

    public void setnValue(String nValue) {
        this.nValue = nValue;
    }
}
