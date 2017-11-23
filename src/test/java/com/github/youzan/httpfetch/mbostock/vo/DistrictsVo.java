package com.github.youzan.httpfetch.mbostock.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by daiqiang on 17/6/9.
 */
public class DistrictsVo {

    @JSONField(name="type")
    private String type;

    @JSONField(name="bbox")
    private List<Double> bbox;

    @JSONField(name="geometries")
    private List<GeometriesVo> geometries;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public List<GeometriesVo> getGeometries() {
        return geometries;
    }

    public void setGeometries(List<GeometriesVo> geometries) {
        this.geometries = geometries;
    }

}
