package com.github.nezha.httpfetch.youzan.api;

import com.github.nezha.httpfetch.HttpApi;
import com.github.nezha.httpfetch.chains.BeanParam;
import com.github.nezha.httpfetch.youzan.vo.GetTeamByIdsRequestVo;

import java.util.Map;

/**
 * Created by daiqiang on 17/9/7.
 */
public interface AccountTeamApi {

    @HttpApi(timeout = 2000, url = "http://api.koudaitong.com/account/team/getTeamByIds")
    Map getTeamByIds(@BeanParam GetTeamByIdsRequestVo vo);

}
