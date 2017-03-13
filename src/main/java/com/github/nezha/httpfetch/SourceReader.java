package com.github.nezha.httpfetch;

import com.github.nezha.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.nezha.httpfetch.interceptor.HttpApiInterceptor;
import com.github.nezha.httpfetch.resolver.MethodParameterResolver;

import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 16/12/6.
 */
public interface SourceReader {

    List<HttpApiInterceptor> getInterceptors();

    List<ResponseGeneratorConvertor> getHandlers();

    List<MethodParameterResolver> getParameterResolvers();

    Map<String, String> getUrlAlias();

}
