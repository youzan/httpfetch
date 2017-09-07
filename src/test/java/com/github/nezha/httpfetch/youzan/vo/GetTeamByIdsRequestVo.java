package com.github.nezha.httpfetch.youzan.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.nezha.httpfetch.resolver.ArrayParam;

import java.util.List;

/**
 * Created by daiqiang on 17/3/28.
 */
public class GetTeamByIdsRequestVo {

    @JSONField(name="kdt_id")
    @ArrayParam
    private List<Long> kdtIds;

    @JSONField(name="debug")
    private String debug;

    public List<Long> getKdtIds() {
        return kdtIds;
    }

    public void setKdtIds(List<Long> kdtIds) {
        this.kdtIds = kdtIds;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }
}
