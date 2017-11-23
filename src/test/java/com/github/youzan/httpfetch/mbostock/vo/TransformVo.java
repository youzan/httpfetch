package com.github.youzan.httpfetch.mbostock.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by daiqiang on 17/6/9.
 */
public class TransformVo {

    @JSONField(name="scale")
    private List<Double> scale;

    @JSONField(name="translate")
    private List<Double> translate;

    public List<Double> getScale() {
        return scale;
    }

    public void setScale(List<Double> scale) {
        this.scale = scale;
    }

    public List<Double> getTranslate() {
        return translate;
    }

    public void setTranslate(List<Double> translate) {
        this.translate = translate;
    }
}
