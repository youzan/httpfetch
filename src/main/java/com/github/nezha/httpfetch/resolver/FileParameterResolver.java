package com.github.nezha.httpfetch.resolver;

import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.ParameterWrapper;

/**
 * Created by daiqiang on 16/12/21.
 */
public class FileParameterResolver implements MethodParameterResolver {

    @Override
    public boolean supperts(HttpApiMethodWrapper wrapper, ParameterWrapper parameter) {
        return parameter.hasAnnotation(FormParam.class);
    }

    @Override
    public void resolveArgument(HttpApiRequestParam param, ParameterWrapper parameter, HttpApiMethodWrapper wrapper, Object arg) {
        //封装FORM类型参数
        param.addFormParam(parameter.getParamName(), arg);
    }

}
