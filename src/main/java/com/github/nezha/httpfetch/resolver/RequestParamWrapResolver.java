package com.github.nezha.httpfetch.resolver;

import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.ParameterWrapper;
import com.github.nezha.httpfetch.RequestParameter;

/**
 * Created by daiqiang on 17/6/14.
 */
public class RequestParamWrapResolver implements MethodParameterResolver {

    @Override
    public boolean supperts(HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
        return true;
    }

    @Override
    public void resolveArgument(HttpApiRequestParam param, HttpApiMethodWrapper wrapper, RequestParameter requestParameter) {
        ParameterWrapper parameterWrapper = requestParameter.getParameterWrapper();
        Object arg = requestParameter.getParameter();
        if(parameterWrapper.hasAnnotation(FormParam.class)){
            param.addFormParam(parameterWrapper.getParamName(), arg);
        }else if(parameterWrapper.hasAnnotation(PostParam.class)){
            param.addPostParam(parameterWrapper.getParamName(), String.valueOf(arg));
        }else if(parameterWrapper.hasAnnotation(RequestBody.class)
                && arg.getClass().isArray() && arg.getClass().getComponentType().equals(Byte.class)){
            param.setRequestBody((byte[]) arg);
        }else{
            param.addGetParam(parameterWrapper.getParamName(), String.valueOf(arg));
        }
    }

}
