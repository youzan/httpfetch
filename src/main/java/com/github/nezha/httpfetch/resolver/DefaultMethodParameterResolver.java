package com.github.nezha.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.github.nezha.httpfetch.CommonUtils;
import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.ParameterWrapper;

import java.io.File;

/**
 * 默认参数封装类,仅对其转换成json
 */
public class DefaultMethodParameterResolver implements MethodParameterResolver {

	@Override
	public boolean supperts(HttpApiMethodWrapper wrapper, ParameterWrapper parameter) {
		return true;
	}

	@Override
	public void resolveArgument(HttpApiRequestParam param,
								ParameterWrapper parameterWrapper, HttpApiMethodWrapper wrapper,
								Object arg) {
		Class<?> parameterCls = parameterWrapper.getParameterType();
		if(parameterCls == null || arg == null){
			return;
		}

		PostParam postParam = parameterWrapper.getAnnotation(PostParam.class);
		FormParam formParam = parameterWrapper.getAnnotation(FormParam.class);
		if(formParam != null) {
			if(arg instanceof File){
				param.addFormParam(parameterWrapper.getParamName(), arg);
			}else{
				param.addFormParam(parameterWrapper.getParamName(), parseParameter(arg));
			}
		}else if(postParam != null){
			param.addPostParam(parameterWrapper.getParamName(), parseParameter(arg));
		}else{
			param.addGetParam(parameterWrapper.getParamName(), parseParameter(arg));
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
