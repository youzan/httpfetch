package com.github.nezha.httpfetch.resolver;

import com.github.nezha.httpfetch.HttpApiMethodWrapper;
import com.github.nezha.httpfetch.HttpApiRequestParam;
import com.github.nezha.httpfetch.MethodParameter;

import java.io.File;

/**
 * Created by daiqiang on 16/12/21.
 */
public class FileParameterResolver implements MethodParameterResolver {

    @Override
    public boolean supperts(HttpApiMethodWrapper wrapper, MethodParameter parameter) {
        return parameter.hasAnnotation(FileParam.class) && File.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public void resolveArgument(HttpApiRequestParam param, MethodParameter parameter, HttpApiMethodWrapper wrapper, Object arg) {
        //封装文件类型参数
        FileParam fileParam = parameter.getAnnotation(FileParam.class);
        param.addFileParam(fileParam.value(), (File) arg);
    }

}
