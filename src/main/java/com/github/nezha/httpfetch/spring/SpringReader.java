package com.github.nezha.httpfetch.spring;

import com.youzan.bigdata.dspadapter.util.httpapi.SourceReader;
import com.youzan.bigdata.dspadapter.util.httpapi.handler.ResponseGeneratorHandler;
import com.youzan.bigdata.dspadapter.util.httpapi.interceptor.HttpApiInterceptor;
import com.youzan.bigdata.dspadapter.util.httpapi.resolver.MethodParameterResolver;

import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 17/5/25.
 */
public class SpringReader implements SourceReader {

    List<HttpApiInterceptor> interceptors;

    List<ResponseGeneratorHandler> handlers;

    List<MethodParameterResolver> parameterResolvers;

    Map<String, String> urlAlias;

    @Override
    public List<HttpApiInterceptor> getInterceptors() {
        return interceptors;
    }

    @Override
    public List<ResponseGeneratorHandler> getHandlers() {
        return handlers;
    }

    @Override
    public List<MethodParameterResolver> getParameterResolvers() {
        return parameterResolvers;
    }

    @Override
    public Map<String, String> getUrlAlias() {
        return urlAlias;
    }

    public void setInterceptors(List<HttpApiInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void setHandlers(List<ResponseGeneratorHandler> handlers) {
        this.handlers = handlers;
    }

    public void setParameterResolvers(List<MethodParameterResolver> parameterResolvers) {
        this.parameterResolvers = parameterResolvers;
    }

    public void setUrlAlias(Map<String, String> urlAlias) {
        this.urlAlias = urlAlias;
    }

}
