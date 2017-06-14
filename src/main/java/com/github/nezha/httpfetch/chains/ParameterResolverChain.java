package com.github.nezha.httpfetch.chains;

import com.github.nezha.httpfetch.*;
import com.github.nezha.httpfetch.resolver.MethodParameterResolver;

import java.util.List;

/**
 * Created by daiqiang on 17/6/13.
 */
public class ParameterResolverChain implements HttpApiChain {

    private HttpApiConfiguration configuration;

    public ParameterResolverChain(HttpApiConfiguration configuration){
        this.configuration = configuration;
    }

    @Override
    public HttpResult doChain(HttpApiInvoker invoker, Invocation invocation) {
        HttpApiMethodWrapper wrapper = invocation.getWrapper();
        HttpApiRequestParam requestParam = invocation.getRequestParam();
        List<RequestParameter> requestParameters = invocation.getParameters();
        if(requestParameters != null){
            //遍历所有的参数
            for(RequestParameter requestParameter : requestParameters){
                choiseAndExecuteResolver(requestParam, wrapper, requestParameter);
            }
        }
        return invoker.invoke(invocation);
    }

    /**
     * 筛选并执行resolver解析参数
     * @param wrapper
     * @param requestParameter
     * @return
     */
    private void choiseAndExecuteResolver(HttpApiRequestParam requestParam, HttpApiMethodWrapper wrapper, RequestParameter requestParameter){
        ParameterWrapper parameterWrapper = requestParameter.getParameterWrapper();
        MethodParameterResolver parameterResolversCache = parameterWrapper.getParameterResolver();
        if(parameterResolversCache == null){
            //遍历已注册的入参处理类
            for(MethodParameterResolver parameterResolver : configuration.getParameterResolvers()){
                if(parameterResolver.supperts(wrapper, requestParameter)){
                    //如果支持则注册到缓存中
                    parameterResolver.resolveArgument(requestParam, wrapper, requestParameter);
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return 8000;
    }
}
