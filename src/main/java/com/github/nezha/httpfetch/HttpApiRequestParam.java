package com.github.nezha.httpfetch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daiqiang on 16/12/6.
 */
public class HttpApiRequestParam {

    private Map<String, String> param = new HashMap<>();

    private Map<String, File> fileParam = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    private String encoding;

    private String url;

    private byte[] requestBody;

    public HttpApiRequestParam(String url){
        this.url = url;
    }

    public void addParam(String key, String value){
        param.put(key, value);
    }

    public void addFileParam(String key, File value){
        fileParam.put(key, value);
    }

    public void removeParam(String key){
        param.remove(key);
    }

    public void addHeaders(String key, String value){
        this.headers.put(key, value);
    }

    public Map<String, String> getParam() {
        return param;
    }

    public Map<String, File> getFileParam() {
        return fileParam;
    }

    public Map<String, String> getHeaders() {
        return headers;
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

    public byte[] getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }
}
