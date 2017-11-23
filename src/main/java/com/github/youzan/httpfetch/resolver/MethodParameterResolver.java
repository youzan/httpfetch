package com.github.youzan.httpfetch.resolver;

import com.github.youzan.httpfetch.HttpApiMethodWrapper;
import com.github.youzan.httpfetch.HttpApiRequestParam;
import com.github.youzan.httpfetch.RequestParameter;

/**
 * http参数解析器
 * @author 11047530
 *
 */
public interface MethodParameterResolver {

	/**
	 * 校验该参数是否支持
	 * @param requestParameter
	 * @return true 支持 false 不支持
	 */
	boolean supperts(HttpApiMethodWrapper wrapper, RequestParameter requestParameter);
	
	/**
	 * 
	 * 参数转化
	 * @param param 上一层处理后的请求参数
	 * @param wrapper api封装后的bean，包括url、参数名称、响应类
	 * @param requestParameter 单个参数
	 * @return 是否添加到请求参数中 true 添加到http param  false 不添加到http param
	 */
	boolean resolveArgument(HttpApiRequestParam param,
						 HttpApiMethodWrapper wrapper,
						 RequestParameter requestParameter);
	
}
