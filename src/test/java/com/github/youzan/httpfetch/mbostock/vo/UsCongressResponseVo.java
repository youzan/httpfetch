package com.github.youzan.httpfetch.mbostock.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by daiqiang on 17/6/9.
 */
public class UsCongressResponseVo {

    @JSONField(name="type")
    private String type;

    @JSONField(name="objects")
    private ObjectsVo objects;

    @JSONField(name="arcs")
    private List<List<List<Integer>>> arcs;

    @JSONField(name="transform")
    private TransformVo transform;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ObjectsVo getObjects() {
        return objects;
    }

    public void setObjects(ObjectsVo objects) {
        this.objects = objects;
    }

    public List<List<List<Integer>>> getArcs() {
        return arcs;
    }

    public void setArcs(List<List<List<Integer>>> arcs) {
        this.arcs = arcs;
    }

    public TransformVo getTransform() {
        return transform;
    }

    public void setTransform(TransformVo transform) {
        this.transform = transform;
    }

}
