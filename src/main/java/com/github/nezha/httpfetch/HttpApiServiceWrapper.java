package com.github.nezha.httpfetch;

import com.github.nezha.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.nezha.httpfetch.interceptor.HttpApiInterceptor;
import com.github.nezha.httpfetch.resolver.MethodParameterResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 16/12/6.
 */
public class HttpApiServiceWrapper {

    private List<SourceReader> sourceReaders = new ArrayList<SourceReader>();

    /**
     * 请求拦截器,做切面处理
     */
    private List<HttpApiInterceptor> interceptors = new ArrayList<HttpApiInterceptor>();

    /**
     * 结果集处理类，如果需要不同的结果转换时，可以继承并注册
     */
    private List<ResponseGeneratorConvertor> handlers = new ArrayList<ResponseGeneratorConvertor>();

    /**
     * 入参处理类，如果需要扩展参数的转换方式时可以继承并注册
     */
    private List<MethodParameterResolver> parameterResolvers = new ArrayList<MethodParameterResolver>();

    private Map<String, String> urlAlias = new HashMap<String, String>();

    public void addReader(SourceReader reader) {
        sourceReaders.add(reader);
    }

    public void init() {
        if (!sourceReaders.isEmpty()) {
            for (SourceReader reader : sourceReaders) {
                if (reader != null) {
                    if (reader.getInterceptors() != null) {
                        interceptors.addAll(reader.getInterceptors());
                    }
                    if (reader.getHandlers() != null) {
                        handlers.addAll(reader.getHandlers());
                    }
                    if (reader.getParameterResolvers() != null) {
                        parameterResolvers.addAll(reader.getParameterResolvers());
                    }
                    if (reader.getUrlAlias() != null) {
                        urlAlias.putAll(reader.getUrlAlias());
                    }
                }
            }
        }
    }

    public List<HttpApiInterceptor> getInterceptors() {
        return interceptors;
    }

    public List<ResponseGeneratorConvertor> getHandlers() {
        return handlers;
    }

    public List<MethodParameterResolver> getParameterResolvers() {
        return parameterResolvers;
    }

    public Map<String, String> getUrlAlias() {
        return urlAlias;
    }

    public List<SourceReader> getSourceReaders() {
        return sourceReaders;
    }
}
