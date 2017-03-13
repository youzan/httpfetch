package com.github.nezha.httpfetch;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpApi {

	/**
	 * 用于函数到url的映射,如果为空的话,会通过接口类名和函数名拼接在一起
	 * 从httpApi.xml中找出匹配的url:这样的做法是因为很多接口会在不同的环境下有不同的host
	 * @return
     */
	String operateCode() default "";

	/**
	 * 请求的METHOD
	 * @return
     */
	String method() default "GET";
	
	Class<?> responseCls() default Object.class;

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

}
