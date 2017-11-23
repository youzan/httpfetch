package com.github.youzan.httpfetch.spring;


import com.github.youzan.httpfetch.SourceReader;
import com.github.youzan.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.youzan.httpfetch.chains.HttpApiChain;
import com.github.youzan.httpfetch.resolver.MethodParameterResolver;

import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 17/5/25.
 */
public class SpringReader implements SourceReader {

    List<HttpApiChain> chains;

    List<ResponseGeneratorConvertor> convertors;

    List<MethodParameterResolver> parameterResolvers;

    Map<String, String> urlAlias;

    @Override
    public List<HttpApiChain> getChains() {
        return chains;
    }

    public void setChains(List<HttpApiChain> chains) {
        this.chains = chains;
    }

    @Override
    public List<ResponseGeneratorConvertor> getConvertors() {
        return convertors;
    }

    @Override
    public List<MethodParameterResolver> getParameterResolvers() {
        return parameterResolvers;
    }

    @Override
    public Map<String, String> getUrlAlias() {
        return urlAlias;
    }


    public void setConvertors(List<ResponseGeneratorConvertor> convertors) {
        this.convertors = convertors;
    }

    public void setParameterResolvers(List<MethodParameterResolver> parameterResolvers) {
        this.parameterResolvers = parameterResolvers;
    }

    public void setUrlAlias(Map<String, String> urlAlias) {
        this.urlAlias = urlAlias;
    }

}
