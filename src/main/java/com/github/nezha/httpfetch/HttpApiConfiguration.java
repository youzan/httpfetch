package com.github.nezha.httpfetch;

import com.github.nezha.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.nezha.httpfetch.chains.HttpApiChain;
import com.github.nezha.httpfetch.resolver.MethodParameterResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 16/12/6.
 */
public class HttpApiConfiguration {

    private List<SourceReader> sourceReaders = new ArrayList<>();

    /**
     * 请求拦截器,做切面处理
     */
    private List<HttpApiChain> chains = new ArrayList<>();

    /**
     * 结果集处理类，如果需要不同的结果转换时，可以继承并注册
     */
    private List<ResponseGeneratorConvertor> handlers = new ArrayList<>();

    /**
     * 入参处理类，如果需要扩展参数的转换方式时可以继承并注册
     */
    private List<MethodParameterResolver> parameterResolvers = new ArrayList<>();

    private Map<String, String> urlAlias = new HashMap<>();

    public void init() {
        if (!sourceReaders.isEmpty()) {
            for (SourceReader reader : sourceReaders) {
                if (reader != null) {
                    if (reader.getChains() != null) {
                        chains.addAll(reader.getChains());
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

    public List<HttpApiChain> getChains() {
        return chains;
    }

    public List<ResponseGeneratorConvertor> getConvertors() {
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

    public void setSourceReaders(List<SourceReader> sourceReaders) {
        this.sourceReaders = sourceReaders;
    }

}
