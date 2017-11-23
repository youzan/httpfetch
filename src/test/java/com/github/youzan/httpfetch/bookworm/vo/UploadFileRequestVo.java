package com.github.youzan.httpfetch.bookworm.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.File;

/**
 * Created by daiqiang on 17/6/14.
 */
public class UploadFileRequestVo {

    @JSONField(name = "file")
    private File file;

    private String name;

    @JSONField(name="n_value")
    private String nValue;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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
