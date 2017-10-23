package com.github.nezha.httpfetch.youzan.api;

import com.alibaba.fastjson.JSON;
import com.github.nezha.httpfetch.BaseTest;
import com.github.nezha.httpfetch.youzan.vo.GetTeamByIdsRequestVo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by daiqiang on 17/9/7.
 */
public class AccountTeamApiTest extends BaseTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountTeamApiTest.class);

    @Autowired
    private AccountTeamApi accountTeamApi;

    @Test
    public void testGetTeamByIds() throws Exception {
        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");

        GetTeamByIdsRequestVo requestVo = new GetTeamByIdsRequestVo();
        requestVo.setDebug("json");
        requestVo.setKdtIds(Arrays.asList(1L));
        Map map = accountTeamApi.getTeamByIds(requestVo);
        System.out.println(JSON.toJSONString(map));
    }

}