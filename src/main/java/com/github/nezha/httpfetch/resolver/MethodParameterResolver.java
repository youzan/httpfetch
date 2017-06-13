package com.github.nezha.httpfetch.resolver;

import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.ParameterWrapper;

/**
 * http参数解析器
 * @author 11047530
 *
 */
public interface MethodParameterResolver {

	/**
	 * 校验该参数是否支持
	 * @param parameter
	 * @return true 支持 false 不支持
	 */
	boolean supperts(HttpApiMethodWrapper wrapper, ParameterWrapper parameter);
	
	/**
	 * 
	 * 参数转化
	 * @param param 上一层处理后的请求参数
	 * @param "method" httpapi函数
	 * @param wrapper api封装后的bean，包括url、参数名称、响应类
	 * @param "name" 入参名称
	 * @param arg  入参值
	 * @return
	 */
	void resolveArgument(HttpApiRequestParam param,
						 ParameterWrapper parameter,
						 HttpApiMethodWrapper wrapper, Object arg);
	
}
