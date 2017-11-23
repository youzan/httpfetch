package com.github.youzan.httpfetch;

import com.github.youzan.httpfetch.convertor.ResponseGeneratorConvertor;
import com.github.youzan.httpfetch.chains.HttpApiChain;
import com.github.youzan.httpfetch.resolver.MethodParameterResolver;

import java.util.List;
import java.util.Map;

/**
 * Created by daiqiang on 16/12/6.
 */
public interface SourceReader {

    List<HttpApiChain> getChains();

    List<ResponseGeneratorConvertor> getConvertors();

    List<MethodParameterResolver> getParameterResolvers();

    Map<String, String> getUrlAlias();

}
