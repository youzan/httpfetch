package com.github.nezha.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.MethodParameter;

/**
 * 默认参数封装类,仅对其转换成json
 */
public class DefaultMethodParameterResolver implements MethodParameterResolver {

	@Override
	public boolean supperts(HttpApiMethodWrapper wrapper, MethodParameter parameter) {
		return true;
	}

	@Override
	public void resolveArgument(HttpApiRequestParam param,
			MethodParameter methodParameter, HttpApiMethodWrapper wrapper,
			Object arg) {
		Class<?> parameterCls = methodParameter.getParameterType();
		if(parameterCls == null || arg == null){
			return;
		}

		if(String.class.isAssignableFrom(parameterCls)){
			param.addParam(methodParameter.getParamName(), arg.toString());
		}else if(parameterCls.isPrimitive()) {
			param.addParam(methodParameter.getParamName(), String.valueOf(arg));
		}else if(parameterCls.isEnum()){
			param.addParam(methodParameter.getParamName(), arg.toString());
		}else{
			param.addParam(methodParameter.getParamName(), JSONObject.toJSONString(arg));
		}
	}

}
