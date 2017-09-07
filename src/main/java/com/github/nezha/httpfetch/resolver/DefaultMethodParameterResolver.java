package com.github.nezha.httpfetch.resolver;

import com.alibaba.fastjson.JSONObject;
import com.github.nezha.httpfetch.*;

import java.awt.*;
import java.awt.image.BufferedImage;
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
	public boolean resolveArgument(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
		ParameterWrapper parameterWrapper = requestParameter.getParameterWrapper();
		Object arg = requestParameter.getParameter();
		if(parameterWrapper.getParameterType() == null || arg == null){
			return false;
		}

		if(arg instanceof File){
			requestParameter.setParameter(arg);
		}else if(arg instanceof java.net.URL){
			requestParameter.setParameter(arg);
		}else if(arg instanceof ImageParam){
			requestParameter.setParameter(arg);
		}else{
			requestParameter.setParameter(ParameterUtils.parseParameter(arg));
		}
		return true;
	}

}
