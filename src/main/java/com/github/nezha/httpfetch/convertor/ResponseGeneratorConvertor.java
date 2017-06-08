package com.github.nezha.httpfetch.convertor;


import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;

import java.lang.reflect.Method;


/**
 * http响应结果处理接口
 * @author 11047530
 *
 */
public interface ResponseGeneratorConvertor {

	boolean supports(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam param, Class<?> responseCls);

	Object generate(Method method, HttpApiMethodWrapper wrapper, HttpApiRequestParam param, byte[] response, Class<?> responseCls);

}
