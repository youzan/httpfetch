package com.github.nezha.httpfetch;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daiqiang on 16/12/6.
 */
public class HttpApiRequestParam {

    private Map<String, String> getParam = new HashMap<>();

    private Map<String, String> postParam = new HashMap<>();

    private Map<String, Object> formParam = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    private String encoding;

    private String url;

    @JSONField(serialize = false, deserialize = false)
    private byte[] requestBody;

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

    public Map<String, String> getGetParam() {
        return getParam;
    }

    public void setGetParam(Map<String, String> getParam) {
        this.getParam = getParam;
    }

    public Map<String, String> getPostParam() {
        return postParam;
    }

    public void setPostParam(Map<String, String> postParam) {
        this.postParam = postParam;
    }

    public Map<String, Object> getFormParam() {
        return formParam;
    }

    public void setFormParam(Map<String, Object> formParam) {
        this.formParam = formParam;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }
}
