package com.github.youzan.httpfetch.mbostock.vo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by daiqiang on 17/6/9.
 */
public class ObjectsVo {

    @JSONField(name="districts")
    private DistrictsVo districts;

    public DistrictsVo getDistricts() {
        return districts;
    }

    public void setDistricts(DistrictsVo districts) {
        this.districts = districts;
    }
}
