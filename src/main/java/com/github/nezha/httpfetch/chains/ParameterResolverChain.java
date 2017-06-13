package com.github.nezha.httpfetch.chains;

import com.github.nezha.httpfetch.*;
import com.github.nezha.httpfetch.resolver.MethodParameterResolver;

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
        Object[] args = invocation.getArgs();
        //封装成请求参数
        MethodParameter[] parameters = wrapper.getParameters();
        if(parameters != null){
            for(int i=0;i<parameters.length;i++){
                MethodParameter parameter = parameters[i];
                MethodParameterResolver parameterResolversCache = parameter.getParameterResolver();
                if(parameterResolversCache == null){
                    //遍历已注册的入参处理类
                    for(MethodParameterResolver parameterResolver : configuration.getParameterResolvers()){
                        if(parameterResolver.supperts(wrapper, parameter)){
                            //如果支持则注册到缓存中
                            parameterResolversCache = parameterResolver;
                            break;
                        }
                    }

                    parameter.setParameterResolver(parameterResolversCache);
                }

                if(parameterResolversCache != null){
                    parameterResolversCache.resolveArgument(requestParam, parameters[i], wrapper, args[i]);
                }
            }
        }
        return invoker.invoke(invocation);
    }

    @Override
    public int getOrder() {
        return 8000;
    }
}
