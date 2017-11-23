package com.github.youzan.httpfetch.mbostock.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by daiqiang on 17/6/9.
 */
public class GeometriesVo {

    @JSONField(name="type")
    private String type;

    @JSONField(name="id")
    private Integer id;

    @JSONField(name="arcs")
    private List<Object> arcs;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Object> getArcs() {
        return arcs;
    }

    public void setArcs(List<Object> arcs) {
        this.arcs = arcs;
    }

}
