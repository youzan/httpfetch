package com.github.youzan.httpfetch;

import com.github.youzan.httpfetch.chains.HttpApiChain;
import com.github.youzan.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.youzan.httpfetch.resolver.MethodParameterResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author dq
 * @Description
 * @Date Created in 19:45 2018/10/25
 * @Modify By:
 **/
public class BeanSourceReader implements SourceReader{

    List<HttpApiChain> httpApiChains = new ArrayList<>();

    List<ResponseGeneratorConvertor> convertors = new ArrayList<>();

    List<MethodParameterResolver> parameterResolvers = new ArrayList<>();

    Map<String, String> urlAlias = new HashMap<>();

    @Override
    public List<HttpApiChain> getChains() {
        return httpApiChains;
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
}
