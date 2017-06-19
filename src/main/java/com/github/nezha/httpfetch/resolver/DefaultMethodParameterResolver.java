package com.github.nezha.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.github.nezha.httpfetch.*;

import java.io.File;

/**
 * 默认参数封装类,仅对其转换成json
 */
public class DefaultMethodParameterResolver implements MethodParameterResolver {

	@Override
	public boolean supperts(HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
		return true;
	}

	@Override
	public void resolveArgument(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
		ParameterWrapper parameterWrapper = requestParameter.getParameterWrapper();
		Object arg = requestParameter.getParameter();
		if(parameterWrapper.getParameterType() == null || arg == null){
			return;
		}

		if(arg instanceof File){
			requestParameter.setParameter(arg);
		}else{
			requestParameter.setParameter(parseParameter(arg));
		}
	}

	private String parseParameter(Object value){
		Class parameterCls = value.getClass();
		if(String.class.isAssignableFrom(parameterCls)){
			return value.toString();
		}else if(CommonUtils.isPrimitive(parameterCls)){
			return String.valueOf(value);
		}else if(parameterCls.isEnum()){
			return value.toString();
		}else {
			return JSONObject.toJSONString(value);
		}
	}

}
