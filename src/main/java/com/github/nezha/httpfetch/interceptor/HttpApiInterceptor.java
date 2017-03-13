package com.github.nezha.httpfetch.interceptor;

import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;

/**
 * Created by daiqiang on 16/12/8.
 */
public interface HttpApiInterceptor {

    /**
     * 提前处理
     */
    void preHandle(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, Object[] args);

    /**
     * 请求前处理
     */
    void preRequest(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, Object[] args);


    /**
     * 请求后处理
     */
    byte[] afterRequest(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, Object[] args, byte[] responseBody);


    /**
     * 返回结果前处理
     */
    Object beforeReturn(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, Object[] args, Object response);


    /**
     * 请求发生异常时
     */
    Object catchError(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, Object[] args,
                      byte[] response, Exception ex);

}
