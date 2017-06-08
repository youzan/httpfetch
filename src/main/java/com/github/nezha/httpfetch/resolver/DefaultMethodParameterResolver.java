package com.github.nezha.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.github.nezha.httpfetch.CommonUtils;
import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.MethodParameter;

import java.io.File;

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

		PostParam postParam = methodParameter.getAnnotation(PostParam.class);
		FormParam formParam = methodParameter.getAnnotation(FormParam.class);
		if(formParam != null) {
			if(arg instanceof File){
				param.addFormParam(methodParameter.getParamName(), arg);
			}else{
				param.addFormParam(methodParameter.getParamName(), parseParameter(arg));
			}
		}else if(postParam != null){
			param.addPostParam(methodParameter.getParamName(), parseParameter(arg));
		}else{
			param.addGetParam(methodParameter.getParamName(), parseParameter(arg));
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
