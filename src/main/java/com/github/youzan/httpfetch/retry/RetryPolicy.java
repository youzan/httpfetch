package com.github.youzan.httpfetch.retry;

import com.github.youzan.httpfetch.HttpResult;

/**
 * 重试校验接口
 */
public interface RetryPolicy {

    /**
     *
     * @param result http请求结果
     * @param retryTimes 重试次数
     * @param remainRetryTimes  剩余重试次数
     * @return
     */
    boolean needRetry(HttpResult result, int retryTimes, int remainRetryTimes);

}
