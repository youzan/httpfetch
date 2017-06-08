package com.github.nezha.httpfetch;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daiqiang on 16/12/6.
 */
@Data
public class HttpApiRequestParam {

    private Map<String, String> getParam = new HashMap<>();

    private Map<String, String> postParam = new HashMap<>();

    private Map<String, Object> formParam = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    private String encoding;

    private String url;

    @JSONField(serialize = false, deserialize = false)
    private byte[] requestBody;

    public HttpApiRequestParam(String url){
        this.url = url;
    }

    public void addGetParam(String key, String value){
        getParam.put(key, value);
    }

    public void addPostParam(String key, String value){
        postParam.put(key, value);
    }

    public void addFormParam(String key, Object value){
        formParam.put(key, value);
    }

    public void removeGetParam(String key){
        getParam.remove(key);
    }

    public void removePostParam(String key){
        postParam.remove(key);
    }

    public void addHeaders(String key, String value){
        this.headers.put(key, value);
    }

    public void addHeaders(Map<String, String> headers){
        this.headers.putAll(headers);
    }

}
