package com.github.youzan.httpfetch;

import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 17/6/12.
 */
public class HttpResult {

    private Integer statusCode;

    private Object data;

    private Map<String, List<String>> headers;

    private Exception exception;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public boolean hasException() {
        return exception != null;
    }

}
