package com.github.youzan.httpfetch;

import com.github.youzan.httpfetch.retry.ConnectFailureRetryPolicy;
import com.github.youzan.httpfetch.retry.RetryPolicy;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpApi {

	/**
	 * url的优先级比operateCode高;
	 * 如果url为空的话, 通过operateCode或者服务interface+method在配置文件中的aliases节点映射url;
	 * operateCode的使用 主要是因为有一些url的host在不同的环境又不一样的配置
	 * 举例interface+method:BaidiApi.search(String) alia为baiduApi.search
	 * @return
	 */
	String url() default "";

	String operateCode() default "";

	/**
	 * 请求的METHOD
	 * @return
	 */
	String method() default "GET";

	/**
	 * http head
	 * @return
	 */
	Header[] headers() default {};

	/**
	 * 结果生产类,使用类的simpleName就行
	 * @return
	 */
	String generator() default "";

	/**
	 * 超时时间
	 * @return
	 */
	int timeout();

	/**
	 * 超时时间
	 * @return
	 */
	int readTimeout() default  0;

	/**
	 * 编码
	 * @return
	 */
	String encoding() default  "UTF-8";

	/**
	 * 重试次数
	 * @return
	 */
	int retry() default 0;

	/**
	 * 重试次数
	 * @return
	 */
	Class<? extends RetryPolicy> retryPolicy() default ConnectFailureRetryPolicy.class;

}
