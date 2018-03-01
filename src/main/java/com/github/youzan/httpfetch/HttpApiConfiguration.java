package com.github.youzan.httpfetch;

import com.github.youzan.httpfetch.chains.*;
import com.github.youzan.httpfetch.convertor.DefaultResponseGeneratorConvertor;
import com.github.youzan.httpfetch.convertor.JsonPathConvertor;
import com.github.youzan.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.youzan.httpfetch.resolver.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 16/12/6.
 */
public class HttpApiConfiguration {

    private List<SourceReader> sourceReaders;

    /**
     * 请求拦截器,做切面处理
     */
    private List<HttpApiChain> chains;

    /**
     * 结果集处理类，如果需要不同的结果转换时，可以继承并注册
     */
    private List<ResponseGeneratorConvertor> convertors;

    /**
     * 入参处理类，如果需要扩展参数的转换方式时可以继承并注册
     */
    private List<MethodParameterResolver> parameterResolvers;

    private Map<String, String> urlAlias;

    public void init() {
        chains = new ArrayList<>();
        convertors = new ArrayList<>();
        parameterResolvers = new ArrayList<>();
        urlAlias = new HashMap<>();

        if (sourceReaders != null && !sourceReaders.isEmpty()) {
            for (SourceReader reader : sourceReaders) {
                if (reader != null) {
                    if (reader.getChains() != null) {
                        chains.addAll(reader.getChains());
                    }
                    if (reader.getConvertors() != null) {
                        convertors.addAll(reader.getConvertors());
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

        //默认的链
        chains.add(new BeanParamParseChain());
        chains.add(new UrlWrapperChain(this));
        chains.add(new ParameterResolverChain(this));
        chains.add(new GenerateResponseChain(this));
        //chains.add(new JsonParseHttpResultChain(this));
        chains.add(new ExecuteRequestChain());

        //默认参数解析类
        parameterResolvers.add(new UrlParameterResolver());
        parameterResolvers.add(new ArrayParameterResolver());
        parameterResolvers.add(new RequestBodyParameterResolver());
        parameterResolvers.add(new DefaultMethodParameterResolver());

        //默认结果解析器
        convertors.add(new JsonPathConvertor());
        convertors.add(new DefaultResponseGeneratorConvertor());

    }

    public List<HttpApiChain> getChains() {
        return chains;
    }

    public List<ResponseGeneratorConvertor> getConvertors() {
        return convertors;
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
