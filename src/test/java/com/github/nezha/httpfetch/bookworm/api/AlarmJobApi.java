package com.github.nezha.httpfetch.bookworm.api;

import com.github.nezha.httpfetch.Header;
import com.github.nezha.httpfetch.HttpApi;
import com.github.nezha.httpfetch.resolver.RequestBody;

import java.util.Map;

/**
 * Created by daiqiang on 17/6/16.
 */
public interface AlarmJobApi {

    @HttpApi(method = "POST", headers = @Header(key = "Content-type", value = "application/json"), timeout = 2000, url = "http://alert.s.qima-inc.com/api/v1/alert")
    String alert(@RequestBody Map<String, Object> param);

}
