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
            for(RequestParameter requestParameter : requestParameters){

                ParameterWrapper parameterWrapper = requestParameter.getParameterWrapper();
                Object parameter = requestParameter.getParameter();

                MethodParameterResolver parameterResolversCache = choiseResolverAndCache(wrapper, parameterWrapper);

                if(parameterResolversCache != null){
                    parameterResolversCache.resolveArgument(requestParam, parameterWrapper, wrapper, parameter);
                }

            }
        }
        return invoker.invoke(invocation);
    }

    /**
     * 选择或者缓存 参数解析类
     * @param wrapper
     * @param parameterWrapper
     * @return
     */
    private MethodParameterResolver choiseResolverAndCache(HttpApiMethodWrapper wrapper, ParameterWrapper parameterWrapper){
        MethodParameterResolver parameterResolversCache = parameterWrapper.getParameterResolver();
        if(parameterResolversCache == null){
            //遍历已注册的入参处理类
            for(MethodParameterResolver parameterResolver : configuration.getParameterResolvers()){
                if(parameterResolver.supperts(wrapper, parameterWrapper)){
                    //如果支持则注册到缓存中
                    parameterResolversCache = parameterResolver;
                    break;
                }
            }

            parameterWrapper.setParameterResolver(parameterResolversCache);
        }
        return parameterResolversCache;
    }

    @Override
    public int getOrder() {
        return 8000;
    }
}
